package com.fanflow.domain.report;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.report.dto.ReportCreateRequest;
import com.fanflow.domain.report.dto.ReportResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ReportController {

	private final ReportService reportService;

	@PostMapping("/api/reports")
	public ApiResponse<ReportResponse> createReport(@RequestBody @Valid ReportCreateRequest request, @CurrentUser CustomUserDetails userDetails) {
		ReportResponse response = reportService.createReport(userDetails.getUserId(), request);

		return ApiResponse.success("신고가 접수되었습니다.", response);
	}

	@GetMapping("/api/admin/reports")
	public ApiResponse<PageResponse<ReportResponse>> getReports(@RequestParam(required = false) String channelSlug,
			@RequestParam(required = false) String status, @RequestParam(required = false) String targetType,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<ReportResponse> response = reportService.getReports(channelSlug, status, targetType, page, size);

		return ApiResponse.success("관리자 신고 목록 조회에 성공했습니다.", response);
	}

	@PatchMapping("/api/admin/reports/{reportId}/resolve")
	public ApiResponse<Void> resolveReport(@PathVariable Long reportId) {
		reportService.resolveReport(reportId);

		return ApiResponse.success("신고를 처리 완료했습니다.");
	}
}