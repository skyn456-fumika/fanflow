package com.fanflow.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminDashboardResponse {

	private long totalUserCount;
	private long todayUserCount;

	private long totalPostCount;
	private long todayPostCount;
	private long blindPostCount;

	private long totalCommentCount;
	private long todayCommentCount;
	private long blindCommentCount;

	private long pendingReportCount;
}