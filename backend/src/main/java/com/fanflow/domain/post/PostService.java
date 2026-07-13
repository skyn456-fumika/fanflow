package com.fanflow.domain.post;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.board.Board;
import com.fanflow.domain.board.BoardRepository;
import com.fanflow.domain.image.ImageFileService;
import com.fanflow.domain.post.dto.PostCreateRequest;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.domain.post.dto.PostResponse;
import com.fanflow.domain.post.dto.PostUpdateRequest;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.response.PageResponse;
import com.fanflow.global.util.HtmlSanitizer;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

	private final PostRepository postRepository;
	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

	private final HtmlSanitizer htmlSanitizer;
	private final ImageFileService imageFileService;

	@Transactional
	public PostResponse createPost(Long userId, PostCreateRequest request) {
		User writer = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		Board board = boardRepository.findByCode(request.getBoardCode()).orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

		if (!board.isActive()) {
			throw new BusinessException(ErrorCode.BOARD_NOT_ACTIVE);
		}

		if ("NOTICE".equals(board.getCode()) && !writer.isAdmin()) {
			throw new BusinessException(ErrorCode.NOTICE_WRITE_FORBIDDEN);
		}

		boolean notice = "NOTICE".equals(board.getCode());

		String sanitizedContent = htmlSanitizer.sanitize(request.getContent());
		String thumbnailUrl = imageFileService.extractFirstImageUrl(sanitizedContent);

		Post post = Post.builder().board(board).writer(writer).title(request.getTitle()).content(sanitizedContent).thumbnailUrl(thumbnailUrl).build();

		Post savedPost = postRepository.save(post);

		imageFileService.markImagesAsUsedFromHtml(sanitizedContent);

		return PostResponse.from(savedPost);
	}

	public PageResponse<PostListResponse> getPosts(String boardCode, String keyword, int page, int size) {
		if (page < 0) {
			page = 0;
		}

		if (size < 1) {
			size = 10;
		}

		if (size > 50) {
			size = 50;
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("notice"), Sort.Order.desc("createdAt")));

		String normalizedBoardCode = normalize(boardCode);
		String normalizedKeyword = normalize(keyword);

		Page<PostListResponse> posts = postRepository.searchPosts(normalizedBoardCode, normalizedKeyword, pageable).map(PostListResponse::from);

		return PageResponse.from(posts);
	}

	private String normalize(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	@Transactional
	public PostResponse getPost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		post.increaseViewCount();

		return PostResponse.from(post);
	}

	@Transactional
	public PostResponse updatePost(Long postId, Long userId, PostUpdateRequest request) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		if (post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		if (!post.isWriter(userId)) {
			throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
		}

		Set<String> beforeImagePaths = imageFileService.extractPostImagePaths(post.getContent());

		String sanitizedContent = htmlSanitizer.sanitize(request.getContent());
		String thumbnailUrl = imageFileService.extractFirstImageUrl(sanitizedContent);

		Set<String> afterImagePaths = imageFileService.extractPostImagePaths(sanitizedContent);

		beforeImagePaths.removeAll(afterImagePaths);

		imageFileService.deletePostImagesByPaths(beforeImagePaths);

		post.update(request.getTitle(), sanitizedContent, thumbnailUrl);

		imageFileService.markImagesAsUsedFromHtml(sanitizedContent);

		return PostResponse.from(post);
	}

	@Transactional
	public void deletePost(Long postId, Long userId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!post.isWriter(userId) && !user.isAdmin()) {
			throw new BusinessException(ErrorCode.POST_ACCESS_DENIED);
		}

		imageFileService.deleteImagesFromHtml(post.getContent());

		post.delete();
	}
}