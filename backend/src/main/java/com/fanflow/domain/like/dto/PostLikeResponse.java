package com.fanflow.domain.like.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostLikeResponse {

	private Long postId;
	private boolean liked;
	private int likeCount;

	public static PostLikeResponse of(Long postId, boolean liked, int likeCount) {
		return PostLikeResponse.builder().postId(postId).liked(liked).likeCount(likeCount).build();
	}
}