package com.fanflow.domain.bookmark;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.bookmark.dto.BookmarkStatusResponse;
import com.fanflow.domain.post.Post;
import com.fanflow.domain.post.PostRepository;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostBookmarkService {

	private final PostBookmarkRepository postBookmarkRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Transactional
	public BookmarkStatusResponse addBookmark(Long postId, Long userId) {
		Post post = getAvailablePost(postId);

		if (postBookmarkRepository.existsByPost_PostIdAndUser_UserId(postId, userId)) {
			return BookmarkStatusResponse.of(postId, true);
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		PostBookmark bookmark = PostBookmark.builder().post(post).user(user).build();

		try {
			postBookmarkRepository.saveAndFlush(bookmark);
		} catch (DataIntegrityViolationException e) {
			/*
			 * 동시에 같은 북마크 요청이 들어온 경우 DB 유니크 제약조건에서 중복을 막는다.
			 */
		}

		return BookmarkStatusResponse.of(postId, true);
	}

	@Transactional
	public BookmarkStatusResponse removeBookmark(Long postId, Long userId) {
		PostBookmark bookmark = postBookmarkRepository.findByPost_PostIdAndUser_UserId(postId, userId).orElse(null);

		if (bookmark != null) {
			postBookmarkRepository.delete(bookmark);
		}

		return BookmarkStatusResponse.of(postId, false);
	}

	public BookmarkStatusResponse getBookmarkStatus(Long postId, Long userId) {
		getAvailablePost(postId);

		boolean bookmarked = postBookmarkRepository.existsByPost_PostIdAndUser_UserId(postId, userId);

		return BookmarkStatusResponse.of(postId, bookmarked);
	}

	public PageResponse<PostListResponse> getMyBookmarks(Long userId, int page, int size) {
		Pageable pageable = createPageable(page, size);

		Page<PostListResponse> bookmarks = postBookmarkRepository.findMyPostBookmarks(userId, pageable)
				.map(bookmark -> PostListResponse.from(bookmark.getPost()));

		return PageResponse.from(bookmarks);
	}

	private Post getAvailablePost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		if (!post.getBoard().isActive()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		if (!post.getBoard().getChannel().isActive()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		return post;
	}

	private Pageable createPageable(int page, int size) {
		if (page < 0) {
			page = 0;
		}

		if (size < 1) {
			size = 10;
		}

		if (size > 50) {
			size = 50;
		}

		return PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
	}
}