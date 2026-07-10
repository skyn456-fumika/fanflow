package com.fanflow.domain.post.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.post.Post;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostListResponse {

	private Long postId;

	private String boardCode;
	private String boardName;

	private Long writerId;
	private String writerNickname;

	private String title;

	private int viewCount;
	private int likeCount;
	private int commentCount;

	private boolean notice;
	private boolean blind;
	private boolean deleted;

	private LocalDateTime createdAt;

	public static PostListResponse from(Post post) {
		return PostListResponse.builder().postId(post.getPostId()).boardCode(post.getBoard().getCode()).boardName(post.getBoard().getName())
				.writerId(post.getWriter().getUserId()).writerNickname(post.getWriter().getNickname()).title(post.getTitle())
				.viewCount(post.getViewCount()).likeCount(post.getLikeCount()).commentCount(post.getCommentCount()).notice(post.isNotice())
				.blind(post.isBlind()).deleted(post.isDeleted()).createdAt(post.getCreatedAt()).build();
	}
}