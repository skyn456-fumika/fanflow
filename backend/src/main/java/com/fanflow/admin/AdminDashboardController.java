package com.fanflow.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.admin.dto.AdminDashboardResponse;
import com.fanflow.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AdminDashboardController {

	private final AdminDashboardService adminDashboardService;

	@GetMapping("/api/admin/dashboard")
	public ApiResponse<AdminDashboardResponse> getDashboard() {
		AdminDashboardResponse response = adminDashboardService.getDashboard();

		return ApiResponse.success("관리자 대시보드 조회에 성공했습니다.", response);
	}
}