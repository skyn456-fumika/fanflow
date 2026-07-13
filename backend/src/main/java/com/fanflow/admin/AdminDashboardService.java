package com.fanflow.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.admin.dto.AdminDashboardResponse;
import com.fanflow.domain.comment.CommentRepository;
import com.fanflow.domain.post.PostRepository;
import com.fanflow.domain.report.ReportRepository;
import com.fanflow.domain.report.ReportStatus;
import com.fanflow.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminDashboardService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final ReportRepository reportRepository;

	public AdminDashboardResponse getDashboard() {
		LocalDateTime todayStart = LocalDate.now().atStartOfDay();
		LocalDateTime tomorrowStart = todayStart.plusDays(1);

		return AdminDashboardResponse.builder().totalUserCount(userRepository.count())
				.todayUserCount(userRepository.countByCreatedAtBetween(todayStart, tomorrowStart))
				.totalPostCount(postRepository.countByDeletedFalse())
				.todayPostCount(postRepository.countByDeletedFalseAndCreatedAtBetween(todayStart, tomorrowStart))
				.blindPostCount(postRepository.countByDeletedFalseAndBlindTrue()).totalCommentCount(commentRepository.countByDeletedFalse())
				.todayCommentCount(commentRepository.countByDeletedFalseAndCreatedAtBetween(todayStart, tomorrowStart))
				.blindCommentCount(commentRepository.countByDeletedFalseAndBlindTrue())
				.pendingReportCount(reportRepository.countByStatus(ReportStatus.PENDING)).build();
	}
}