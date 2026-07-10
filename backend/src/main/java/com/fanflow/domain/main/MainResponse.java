package com.fanflow.domain.main;

import java.util.List;

import com.fanflow.domain.post.dto.PostListResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MainResponse {

	private List<PostListResponse> noticePosts;
	private List<PostListResponse> popularPosts;
	private List<PostListResponse> recentPosts;
	private List<PostListResponse> commentedPosts;
}