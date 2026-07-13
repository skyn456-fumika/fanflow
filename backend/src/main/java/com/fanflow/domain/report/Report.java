package com.fanflow.domain.report;

import com.fanflow.domain.user.User;
import com.fanflow.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "reports", uniqueConstraints = {
		@UniqueConstraint(name = "uk_report_user_target", columnNames = { "reporter_id", "target_type", "target_id" }) })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Report extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long reportId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reporter_id", nullable = false)
	private User reporter;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReportTargetType targetType;

	@Column(nullable = false)
	private Long targetId;

	@Column(nullable = false, length = 500)
	private String reason;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReportStatus status;

	@Builder
	public Report(User reporter, ReportTargetType targetType, Long targetId, String reason) {
		this.reporter = reporter;
		this.targetType = targetType;
		this.targetId = targetId;
		this.reason = reason;
		this.status = ReportStatus.PENDING;
	}

	public void resolve() {
		this.status = ReportStatus.RESOLVED;
	}

	public boolean isResolved() {
		return this.status == ReportStatus.RESOLVED;
	}
}