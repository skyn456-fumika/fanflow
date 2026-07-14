package com.fanflow.domain.channel.dto;

import java.util.List;

import com.fanflow.domain.board.dto.BoardResponse;
import com.fanflow.domain.post.dto.PostListResponse;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelHomeResponse {

	private ChannelResponse channel;
	private List<BoardResponse> boards;
	private List<PostListResponse> noticePosts;
	private List<PostListResponse> popularPosts;
	private List<PostListResponse> recentPosts;
}