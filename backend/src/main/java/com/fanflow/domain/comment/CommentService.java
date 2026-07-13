package com.fanflow.domain.comment;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.comment.dto.CommentCreateRequest;
import com.fanflow.domain.comment.dto.CommentResponse;
import com.fanflow.domain.notification.NotificationService;
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
public class CommentService {

	private final CommentRepository commentRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;

	private final NotificationService notificationService;

	@Transactional
	public CommentResponse createComment(Long postId, Long userId, CommentCreateRequest request) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		User writer = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		Comment comment = Comment.builder().post(post).writer(writer).content(request.getContent()).build();

		Comment savedComment = commentRepository.save(comment);

		post.increaseCommentCount();

		if (!post.getWriter().getUserId().equals(writer.getUserId())) {
			notificationService.createCommentOnPostNotification(post.getWriter(), post.getPostId(), savedComment.getCommentId(),
					writer.getNickname());
		}

		return CommentResponse.from(savedComment);
	}

	public List<CommentResponse> getComments(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		return commentRepository.findVisibleCommentsByPostId(postId).stream().map(CommentResponse::from).toList();
	}

	@Transactional
	public void deleteComment(Long commentId, Long userId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (comment.isDeleted()) {
			throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
		}

		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!comment.isWriter(userId) && !user.isAdmin()) {
			throw new BusinessException(ErrorCode.COMMENT_ACCESS_DENIED);
		}

		comment.delete();
		comment.getPost().decreaseCommentCount();
	}
}