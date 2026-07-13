package com.fanflow.domain.comment.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.comment.Comment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {

	private Long commentId;
	private Long postId;

	private Long writerId;
	private String writerNickname;

	private String content;

	private boolean blind;
	private boolean deleted;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String writerProfileImageUrl;

	public static CommentResponse from(Comment comment) {
		return CommentResponse.builder().commentId(comment.getCommentId()).postId(comment.getPost().getPostId())
				.writerId(comment.getWriter().getUserId()).writerNickname(comment.getWriter().getNickname()).content(comment.getContent())
				.blind(comment.isBlind()).deleted(comment.isDeleted()).createdAt(comment.getCreatedAt()).updatedAt(comment.getUpdatedAt())
				.writerProfileImageUrl(comment.getWriter().getProfileImageUrl()).build();
	}
}