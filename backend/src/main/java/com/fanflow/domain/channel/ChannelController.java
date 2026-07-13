package com.fanflow.domain.channel;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.channel.dto.ChannelResponse;
import com.fanflow.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChannelController {

	private final ChannelService channelService;

	@GetMapping("/api/channels")
	public ApiResponse<List<ChannelResponse>> getChannels() {
		List<ChannelResponse> response = channelService.getChannels();

		return ApiResponse.success("채널 목록 조회에 성공했습니다.", response);
	}

	@GetMapping("/api/channels/{slug}")
	public ApiResponse<ChannelResponse> getChannel(@PathVariable String slug) {
		ChannelResponse response = channelService.getChannel(slug);

		return ApiResponse.success("채널 조회에 성공했습니다.", response);
	}
}