package com.fanflow.domain.block;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.block.dto.BlockedUserResponse;
import com.fanflow.domain.block.dto.UserBlockStatusResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserBlockController {

	private final UserBlockService userBlockService;

	@PostMapping("/api/users/{userId}/blocks")
	public ApiResponse<UserBlockStatusResponse> blockUser(@PathVariable Long userId, @CurrentUser CustomUserDetails userDetails) {
		UserBlockStatusResponse response = userBlockService.blockUser(userDetails.getUserId(), userId);

		return ApiResponse.success("사용자를 차단했습니다.", response);
	}

	@DeleteMapping("/api/users/{userId}/blocks")
	public ApiResponse<UserBlockStatusResponse> unblockUser(@PathVariable Long userId, @CurrentUser CustomUserDetails userDetails) {
		UserBlockStatusResponse response = userBlockService.unblockUser(userDetails.getUserId(), userId);

		return ApiResponse.success("사용자 차단을 해제했습니다.", response);
	}

	@GetMapping("/api/users/{userId}/blocks/me")
	public ApiResponse<UserBlockStatusResponse> getBlockStatus(@PathVariable Long userId, @CurrentUser CustomUserDetails userDetails) {
		UserBlockStatusResponse response = userBlockService.getBlockStatus(userDetails.getUserId(), userId);

		return ApiResponse.success("사용자 차단 상태 조회에 성공했습니다.", response);
	}

	@GetMapping("/api/users/me/blocks")
	public ApiResponse<PageResponse<BlockedUserResponse>> getMyBlocks(@CurrentUser CustomUserDetails userDetails,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<BlockedUserResponse> response = userBlockService.getMyBlocks(userDetails.getUserId(), page, size);

		return ApiResponse.success("차단 사용자 목록 조회에 성공했습니다.", response);
	}
}