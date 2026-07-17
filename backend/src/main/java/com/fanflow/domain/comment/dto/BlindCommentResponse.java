package com.fanflow.domain.comment.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.comment.Comment;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BlindCommentResponse {

	private Long commentId;
	private Long postId;

	private Long parentCommentId;
	private boolean reply;

	private String channelSlug;
	private String boardCode;
	private String boardName;

	private String postTitle;

	private Long writerId;
	private String writerNickname;

	private String content;
	private LocalDateTime createdAt;

	public static BlindCommentResponse from(Comment comment) {
		return BlindCommentResponse.builder().commentId(comment.getCommentId()).postId(comment.getPost().getPostId())
				.parentCommentId(comment.getParent() == null ? null : comment.getParent().getCommentId()).reply(comment.isReply())
				.channelSlug(comment.getPost().getBoard().getChannel().getSlug()).boardCode(comment.getPost().getBoard().getCode())
				.boardName(comment.getPost().getBoard().getName()).postTitle(comment.getPost().getTitle()).writerId(comment.getWriter().getUserId())
				.writerNickname(comment.getWriter().getNickname()).content(comment.getContent()).createdAt(comment.getCreatedAt()).build();
	}
}