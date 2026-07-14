package com.fanflow.domain.channel;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fanflow.domain.channel.dto.ChannelCreateRequest;
import com.fanflow.domain.channel.dto.ChannelResponse;
import com.fanflow.domain.channel.dto.ChannelUpdateRequest;
import com.fanflow.global.response.ApiResponse;

import jakarta.validation.Valid;
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

	@GetMapping("/api/admin/channels")
	public ApiResponse<List<ChannelResponse>> getAdminChannels() {
		List<ChannelResponse> response = channelService.getAdminChannels();

		return ApiResponse.success("관리자 채널 목록 조회에 성공했습니다.", response);
	}

	@PostMapping("/api/admin/channels")
	public ApiResponse<ChannelResponse> createChannel(@Valid @RequestBody ChannelCreateRequest request) {
		ChannelResponse response = channelService.createChannel(request);

		return ApiResponse.success("채널이 생성되었습니다.", response);
	}

	@PutMapping("/api/admin/channels/{channelId}")
	public ApiResponse<ChannelResponse> updateChannel(@PathVariable Long channelId, @Valid @RequestBody ChannelUpdateRequest request) {
		ChannelResponse response = channelService.updateChannel(channelId, request);

		return ApiResponse.success("채널이 수정되었습니다.", response);
	}

	@PatchMapping("/api/admin/channels/{channelId}/activate")
	public ApiResponse<Void> activateChannel(@PathVariable Long channelId) {
		channelService.activateChannel(channelId);

		return ApiResponse.success("채널이 활성화되었습니다.");
	}

	@PatchMapping("/api/admin/channels/{channelId}/deactivate")
	public ApiResponse<Void> deactivateChannel(@PathVariable Long channelId) {
		channelService.deactivateChannel(channelId);

		return ApiResponse.success("채널이 비활성화되었습니다.");
	}

	@PostMapping("/api/admin/channels/{channelId}/profile-image")
	public ApiResponse<ChannelResponse> uploadProfileImage(@PathVariable Long channelId, @RequestParam("file") MultipartFile file) {

		ChannelResponse response = channelService.uploadProfileImage(channelId, file);

		return ApiResponse.success("채널 프로필 이미지가 업로드되었습니다.", response);
	}

	@PostMapping("/api/admin/channels/{channelId}/banner-image")
	public ApiResponse<ChannelResponse> uploadBannerImage(@PathVariable Long channelId, @RequestParam("file") MultipartFile file) {

		ChannelResponse response = channelService.uploadBannerImage(channelId, file);

		return ApiResponse.success("채널 배너 이미지가 업로드되었습니다.", response);
	}
}