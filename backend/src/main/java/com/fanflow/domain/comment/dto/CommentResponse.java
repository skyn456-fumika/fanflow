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

	private Long parentCommentId;
	private boolean reply;

	private Long channelId;
	private String channelName;
	private String channelSlug;

	private Long boardId;
	private String boardCode;
	private String boardName;

	private String postTitle;

	private Long writerId;
	private String writerNickname;

	private String content;

	private boolean blind;
	private boolean deleted;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String writerProfileImageUrl;

	public static CommentResponse from(Comment comment) {
		boolean deleted = comment.isDeleted();

		return CommentResponse.builder().commentId(comment.getCommentId())
				.parentCommentId(comment.getParent() != null ? comment.getParent().getCommentId() : null).reply(comment.isReply())

				.channelId(comment.getPost().getBoard().getChannel().getChannelId()).channelName(comment.getPost().getBoard().getChannel().getName())
				.channelSlug(comment.getPost().getBoard().getChannel().getSlug())

				.boardId(comment.getPost().getBoard().getBoardId()).boardCode(comment.getPost().getBoard().getCode())
				.boardName(comment.getPost().getBoard().getName())

				.postId(comment.getPost().getPostId()).postTitle(comment.getPost().getTitle())

				.writerId(deleted ? null : comment.getWriter().getUserId()).writerNickname(deleted ? "알 수 없음" : comment.getWriter().getNickname())
				.writerProfileImageUrl(deleted ? null : comment.getWriter().getProfileImageUrl())

				.content(deleted ? "삭제된 댓글입니다." : comment.getContent())

				.blind(comment.isBlind()).deleted(deleted)

				.createdAt(comment.getCreatedAt()).updatedAt(comment.getUpdatedAt()).build();
	}
}