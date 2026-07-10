package com.fanflow.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.admin.dto.AdminUserResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AdminUserController {

	private final AdminUserService adminUserService;

	@GetMapping("/api/admin/users")
	public ApiResponse<PageResponse<AdminUserResponse>> getUsers(@RequestParam(required = false) String status,
			@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<AdminUserResponse> response = adminUserService.getUsers(status, keyword, page, size);

		return ApiResponse.success("관리자 회원 목록 조회에 성공했습니다.", response);
	}

	@PatchMapping("/api/admin/users/{userId}/block")
	public ApiResponse<Void> blockUser(@PathVariable Long userId, @CurrentUser CustomUserDetails userDetails) {
		adminUserService.blockUser(userId, userDetails.getUserId());
		return ApiResponse.success("회원을 정지 처리했습니다.");
	}

	@PatchMapping("/api/admin/users/{userId}/activate")
	public ApiResponse<Void> activateUser(@PathVariable Long userId) {
		adminUserService.activateUser(userId);
		return ApiResponse.success("회원 정지를 해제했습니다.");
	}
}