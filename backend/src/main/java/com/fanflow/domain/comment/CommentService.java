package com.fanflow.domain.comment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.comment.dto.CommentCreateRequest;
import com.fanflow.domain.comment.dto.CommentResponse;
import com.fanflow.domain.comment.dto.CommentUpdateRequest;
import com.fanflow.domain.like.CommentLikeRepository;
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
	private final CommentLikeRepository commentLikeRepository;

	private final NotificationService notificationService;

	@Transactional
	public CommentResponse createComment(Long postId, Long userId, CommentCreateRequest request) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		User writer = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		Comment comment = Comment.builder().post(post).writer(writer).content(request.getContent().trim()).parent(null).build();

		Comment savedComment = commentRepository.save(comment);

		post.increaseCommentCount();

		notificationService.createCommentOnPostNotification(post.getWriter(), post.getPostId(), savedComment.getCommentId(), writer);

		return CommentResponse.from(savedComment);
	}

	public List<CommentResponse> getComments(Long postId, Long userId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		List<Comment> comments = commentRepository.findVisibleCommentsByPostId(postId, userId);

		if (userId == null || comments.isEmpty()) {
			return comments.stream().map(CommentResponse::from).toList();
		}

		List<Long> commentIds = comments.stream().filter(comment -> !comment.isDeleted()).map(Comment::getCommentId).toList();

		Set<Long> likedCommentIds = new HashSet<>(commentLikeRepository.findByComment_CommentIdInAndUser_UserId(commentIds, userId).stream()
				.map(commentLike -> commentLike.getComment().getCommentId()).toList());

		return comments.stream().map(comment -> CommentResponse.from(comment, likedCommentIds.contains(comment.getCommentId()))).toList();
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

	@Transactional
	public CommentResponse updateComment(Long commentId, Long userId, CommentUpdateRequest request) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (comment.isDeleted() || comment.isBlind()) {
			throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
		}

		Post post = comment.getPost();

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		if (!comment.isWriter(userId)) {
			throw new BusinessException(ErrorCode.COMMENT_ACCESS_DENIED);
		}

		String content = request.getContent().trim();

		comment.update(content);

		return CommentResponse.from(comment);
	}

	@Transactional
	public CommentResponse createReply(Long parentCommentId, Long userId, CommentCreateRequest request) {
		Comment parent = commentRepository.findById(parentCommentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (parent.isDeleted() || parent.isBlind()) {
			throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
		}

		if (parent.isReply()) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}

		Post post = parent.getPost();

		if (post.isDeleted() || post.isBlind()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		User writer = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		String content = request.getContent().trim();

		Comment reply = Comment.builder().post(post).writer(writer).content(content).parent(parent).build();

		Comment savedReply = commentRepository.save(reply);

		post.increaseCommentCount();

		User parentWriter = parent.getWriter();

		notificationService.createReplyOnCommentNotification(parent.getWriter(), parent.getPost().getPostId(), savedReply.getCommentId(), writer);

		return CommentResponse.from(savedReply);
	}
}