package com.fanflow.domain.like.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentLikeResponse {

	private Long commentId;
	private boolean liked;
	private long likeCount;

	public static CommentLikeResponse of(Long commentId, boolean liked, long likeCount) {
		return CommentLikeResponse.builder().commentId(commentId).liked(liked).likeCount(likeCount).build();
	}
}