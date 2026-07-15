package com.fanflow.domain.like;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.comment.Comment;
import com.fanflow.domain.comment.CommentRepository;
import com.fanflow.domain.like.dto.CommentLikeResponse;
import com.fanflow.domain.notification.NotificationService;
import com.fanflow.domain.post.Post;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentLikeService {

	private final CommentLikeRepository commentLikeRepository;
	private final CommentRepository commentRepository;
	private final UserRepository userRepository;

	private final NotificationService notificationService;

	@Transactional
	public CommentLikeResponse likeComment(Long commentId, Long userId) {
		Comment comment = getActiveComment(commentId);
		User user = getUser(userId);

		boolean alreadyLiked = commentLikeRepository.existsByComment_CommentIdAndUser_UserId(commentId, userId);

		if (alreadyLiked) {
			throw new BusinessException(ErrorCode.ALREADY_LIKED_COMMENT);
		}

		CommentLike commentLike = CommentLike.builder().comment(comment).user(user).build();

		commentLikeRepository.save(commentLike);
		comment.increaseLikeCount();

		if (!comment.getWriter().getUserId().equals(user.getUserId())) {
			notificationService.createCommentLikedNotification(comment.getWriter(), comment.getPost().getPostId(), comment.getCommentId(), user);
		}

		return CommentLikeResponse.of(comment.getCommentId(), true, comment.getLikeCount());
	}

	@Transactional
	public CommentLikeResponse unlikeComment(Long commentId, Long userId) {
		Comment comment = getActiveComment(commentId);

		CommentLike commentLike = commentLikeRepository.findByComment_CommentIdAndUser_UserId(commentId, userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_LIKE_NOT_FOUND));

		commentLikeRepository.delete(commentLike);
		comment.decreaseLikeCount();

		if (!comment.getWriter().getUserId().equals(userId)) {
			notificationService.deleteCommentLikedNotification(comment.getWriter().getUserId(), comment.getCommentId(), userId);
		}

		return CommentLikeResponse.of(comment.getCommentId(), false, comment.getLikeCount());
	}

	public CommentLikeResponse getMyLikeStatus(Long commentId, Long userId) {
		Comment comment = getActiveComment(commentId);

		boolean liked = commentLikeRepository.existsByComment_CommentIdAndUser_UserId(commentId, userId);

		return CommentLikeResponse.of(comment.getCommentId(), liked, comment.getLikeCount());
	}

	private Comment getActiveComment(Long commentId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (comment.isDeleted() || comment.isBlind()) {
			throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
		}

		Post post = comment.getPost();

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		return comment;
	}

	private User getUser(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
	}
}