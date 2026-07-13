package com.fanflow.domain.report.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.report.Report;
import com.fanflow.domain.report.ReportStatus;
import com.fanflow.domain.report.ReportTargetType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReportResponse {

	private Long reportId;

	private Long reporterId;
	private String reporterEmail;
	private String reporterNickname;

	private ReportTargetType targetType;
	private Long targetId;

	private String reason;
	private ReportStatus status;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	private Long targetPostId;
	private String targetTitle;
	private String targetPreview;

	public static ReportResponse from(Report report, Long targetPostId, String targetTitle, String targetPreview) {
		return ReportResponse.builder().reportId(report.getReportId()).reporterId(report.getReporter().getUserId())
				.reporterEmail(report.getReporter().getEmail()).reporterNickname(report.getReporter().getNickname())
				.targetType(report.getTargetType()).targetId(report.getTargetId()).targetPostId(targetPostId).targetTitle(targetTitle)
				.targetPreview(targetPreview).reason(report.getReason()).status(report.getStatus()).createdAt(report.getCreatedAt())
				.updatedAt(report.getUpdatedAt()).build();
	}
}