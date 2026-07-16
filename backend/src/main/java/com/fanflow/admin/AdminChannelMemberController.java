package com.fanflow.admin;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.channelmember.ChannelMemberService;
import com.fanflow.domain.channelmember.dto.ChannelMemberResponse;
import com.fanflow.domain.channelmember.dto.ChannelOwnerAssignRequest;
import com.fanflow.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AdminChannelMemberController {

	private final ChannelMemberService channelMemberService;

	@GetMapping("/api/admin/channels/{channelId}/members")
	public ApiResponse<List<ChannelMemberResponse>> getMembers(@PathVariable Long channelId) {
		return ApiResponse.success("채널 운영자 목록 조회에 성공했습니다.", channelMemberService.getChannelMembers(channelId));
	}

	@PostMapping("/api/admin/channels/{channelId}/owner")
	public ApiResponse<ChannelMemberResponse> assignOwner(@PathVariable Long channelId, @Valid @RequestBody ChannelOwnerAssignRequest request) {
		return ApiResponse.success("채널 소유자가 지정되었습니다.", channelMemberService.assignOwner(channelId, request.getUserId()));
	}

	@DeleteMapping("/api/admin/channels/{channelId}/owner/{userId}")
	public ApiResponse<Void> removeOwner(@PathVariable Long channelId, @PathVariable Long userId) {
		channelMemberService.removeOwner(channelId, userId);

		return ApiResponse.success("채널 소유자 지정이 해제되었습니다.");
	}
}