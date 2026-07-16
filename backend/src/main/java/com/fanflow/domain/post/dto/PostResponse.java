package com.fanflow.domain.post.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.post.Post;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostResponse {

	private Long postId;

	private Long channelId;
	private String channelName;
	private String channelSlug;

	private Long boardId;
	private String boardCode;
	private String boardName;

	private Long writerId;
	private String writerNickname;

	private String title;
	private String content;

	private int viewCount;
	private int likeCount;
	private int commentCount;

	private boolean notice;
	private boolean blind;
	private boolean deleted;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private String thumbnailUrl;

	private String writerProfileImageUrl;

	private boolean manageableByMe;

	public static PostResponse from(Post post) {
		return from(post, false);
	}

	public static PostResponse from(Post post, boolean manageableByMe) {
		return PostResponse.builder().postId(post.getPostId()).channelId(post.getBoard().getChannel().getChannelId())
				.channelName(post.getBoard().getChannel().getName()).channelSlug(post.getBoard().getChannel().getSlug())
				.boardId(post.getBoard().getBoardId()).boardCode(post.getBoard().getCode()).boardName(post.getBoard().getName())
				.writerId(post.getWriter().getUserId()).writerNickname(post.getWriter().getNickname())
				.writerProfileImageUrl(post.getWriter().getProfileImageUrl()).title(post.getTitle()).content(post.getContent())
				.viewCount(post.getViewCount()).likeCount(post.getLikeCount()).commentCount(post.getCommentCount()).notice(post.isNotice())
				.blind(post.isBlind()).deleted(post.isDeleted()).createdAt(post.getCreatedAt()).updatedAt(post.getUpdatedAt())
				.thumbnailUrl(post.getThumbnailUrl()).manageableByMe(manageableByMe).build();
	}
}