package com.fanflow.domain.like;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.like.dto.PostLikeResponse;
import com.fanflow.domain.post.Post;
import com.fanflow.domain.post.PostRepository;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostLikeService {

	private final PostLikeRepository postLikeRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	@Transactional
	public PostLikeResponse likePost(Long postId, Long userId) {
		Post post = getActivePost(postId);
		User user = getUser(userId);

		if (postLikeRepository.existsByPost_PostIdAndUser_UserId(postId, userId)) {
			throw new BusinessException(ErrorCode.ALREADY_LIKED_POST);
		}

		PostLike postLike = PostLike.builder().post(post).user(user).build();

		postLikeRepository.save(postLike);
		post.increaseLikeCount();

		return PostLikeResponse.of(post.getPostId(), true, post.getLikeCount());
	}

	@Transactional
	public PostLikeResponse unlikePost(Long postId, Long userId) {
		Post post = getActivePost(postId);

		PostLike postLike = postLikeRepository.findByPost_PostIdAndUser_UserId(postId, userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.POST_LIKE_NOT_FOUND));

		postLikeRepository.delete(postLike);
		post.decreaseLikeCount();

		return PostLikeResponse.of(post.getPostId(), false, post.getLikeCount());
	}

	public PostLikeResponse getMyLikeStatus(Long postId, Long userId) {
		Post post = getActivePost(postId);

		boolean liked = postLikeRepository.existsByPost_PostIdAndUser_UserId(postId, userId);

		return PostLikeResponse.of(post.getPostId(), liked, post.getLikeCount());
	}

	private Post getActivePost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		return post;
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}
}