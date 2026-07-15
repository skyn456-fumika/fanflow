package com.fanflow.domain.bookmark.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BookmarkStatusResponse {

	private Long postId;
	private boolean bookmarked;

	public static BookmarkStatusResponse of(Long postId, boolean bookmarked) {
		return BookmarkStatusResponse.builder().postId(postId).bookmarked(bookmarked).build();
	}
}