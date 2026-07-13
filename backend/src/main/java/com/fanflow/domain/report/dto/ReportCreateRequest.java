package com.fanflow.domain.report.dto;

import com.fanflow.domain.report.ReportTargetType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReportCreateRequest {

	@NotNull(message = "신고 대상 타입은 필수입니다.")
	private ReportTargetType targetType;

	@NotNull(message = "신고 대상 ID는 필수입니다.")
	private Long targetId;

	@NotBlank(message = "신고 사유를 입력해주세요.")
	@Size(max = 500, message = "신고 사유는 최대 500자까지 입력할 수 있습니다.")
	private String reason;
}