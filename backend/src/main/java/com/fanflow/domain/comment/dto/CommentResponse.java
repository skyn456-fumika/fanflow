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

	private String writerProfileImageUrl;

	private Long writerId;
	private String writerNickname;

	private String content;

	private long likeCount;
	private boolean likedByMe;

	private boolean blind;
	private boolean deleted;

	private boolean manageableByMe;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static CommentResponse from(Comment comment) {
		return from(comment, false, false);
	}

	public static CommentResponse from(Comment comment, boolean likedByMe) {
		return from(comment, likedByMe, false);
	}

	public static CommentResponse from(Comment comment, boolean likedByMe, boolean manageableByMe) {
		boolean deleted = comment.isDeleted();

		String content;

		if (deleted) {
			content = "삭제된 댓글입니다.";
		} else if (comment.isBlind()) {
			content = "채널 운영진에 의해 블라인드 처리된 댓글입니다.";
		} else {
			content = comment.getContent();
		}

		return CommentResponse.builder().commentId(comment.getCommentId())
				.parentCommentId(comment.getParent() != null ? comment.getParent().getCommentId() : null).reply(comment.isReply())
				.channelId(comment.getPost().getBoard().getChannel().getChannelId()).channelName(comment.getPost().getBoard().getChannel().getName())
				.channelSlug(comment.getPost().getBoard().getChannel().getSlug()).boardId(comment.getPost().getBoard().getBoardId())
				.boardCode(comment.getPost().getBoard().getCode()).boardName(comment.getPost().getBoard().getName())
				.postId(comment.getPost().getPostId()).postTitle(comment.getPost().getTitle())
				.writerId(deleted ? null : comment.getWriter().getUserId()).writerNickname(deleted ? "알 수 없음" : comment.getWriter().getNickname())
				.writerProfileImageUrl(deleted ? null : comment.getWriter().getProfileImageUrl()).content(content)
				.likeCount(deleted || comment.isBlind() ? 0L : comment.getLikeCount()).likedByMe(!deleted && !comment.isBlind() && likedByMe)
				.blind(comment.isBlind()).deleted(deleted).manageableByMe(manageableByMe).createdAt(comment.getCreatedAt())
				.updatedAt(comment.getUpdatedAt()).build();
	}
}