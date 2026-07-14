package com.fanflow.domain.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.comment.Comment;
import com.fanflow.domain.comment.CommentRepository;
import com.fanflow.domain.post.Post;
import com.fanflow.domain.post.PostRepository;
import com.fanflow.domain.report.dto.ReportCreateRequest;
import com.fanflow.domain.report.dto.ReportResponse;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportService {

	private final ReportRepository reportRepository;
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;

	@Transactional
	public ReportResponse createReport(Long reporterId, ReportCreateRequest request) {
		User reporter = userRepository.findById(reporterId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		validateTarget(request.getTargetType(), request.getTargetId());

		boolean alreadyReported = reportRepository.existsByReporter_UserIdAndTargetTypeAndTargetId(reporterId, request.getTargetType(),
				request.getTargetId());

		if (alreadyReported) {
			throw new BusinessException(ErrorCode.ALREADY_REPORTED);
		}

		Report report = Report.builder().reporter(reporter).targetType(request.getTargetType()).targetId(request.getTargetId())
				.reason(request.getReason().trim()).build();

		Report savedReport = reportRepository.save(report);

		return toResponse(savedReport);
	}

	public PageResponse<ReportResponse> getReports(String channelSlug, String status, String targetType, int page, int size) {
		if (page < 0) {
			page = 0;
		}

		if (size < 1) {
			size = 10;
		}

		if (size > 50) {
			size = 50;
		}

		ReportStatus reportStatus = parseStatus(status);
		ReportTargetType reportTargetType = parseTargetType(targetType);

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

		Page<ReportResponse> reports = reportRepository.searchReports(normalize(channelSlug), reportStatus, reportTargetType, pageable)
				.map(this::toResponse);

		return PageResponse.from(reports);
	}

	@Transactional
	public void resolveReport(Long reportId) {
		Report report = reportRepository.findById(reportId).orElseThrow(() -> new BusinessException(ErrorCode.REPORT_NOT_FOUND));

		if (report.isResolved()) {
			throw new BusinessException(ErrorCode.REPORT_ALREADY_RESOLVED);
		}

		report.resolve();
	}

	private void validateTarget(ReportTargetType targetType, Long targetId) {
		if (targetType == ReportTargetType.POST) {
			Post post = postRepository.findById(targetId).orElseThrow(() -> new BusinessException(ErrorCode.REPORT_TARGET_NOT_FOUND));

			if (post.isDeleted() || post.isBlind()) {
				throw new BusinessException(ErrorCode.REPORT_TARGET_NOT_FOUND);
			}

			return;
		}

		if (targetType == ReportTargetType.COMMENT) {
			Comment comment = commentRepository.findById(targetId).orElseThrow(() -> new BusinessException(ErrorCode.REPORT_TARGET_NOT_FOUND));

			if (comment.isDeleted() || comment.isBlind()) {
				throw new BusinessException(ErrorCode.REPORT_TARGET_NOT_FOUND);
			}

			return;
		}

		throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
	}

	private ReportStatus parseStatus(String status) {
		if (status == null || status.isBlank()) {
			return null;
		}

		try {
			return ReportStatus.valueOf(status.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	private ReportTargetType parseTargetType(String targetType) {
		if (targetType == null || targetType.isBlank()) {
			return null;
		}

		try {
			return ReportTargetType.valueOf(targetType.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	private ReportResponse toResponse(Report report) {
		if (report.getTargetType() == ReportTargetType.POST) {
			Post post = postRepository.findById(report.getTargetId()).orElse(null);

			if (post == null) {
				return ReportResponse.from(report, null, "삭제된 게시글", "대상을 찾을 수 없습니다.", null, null, null, null, null, null);
			}

			return ReportResponse.from(report, post.getPostId(), post.getTitle(), createPreview(post.getContent()),
					post.getBoard().getChannel().getChannelId(), post.getBoard().getChannel().getName(), post.getBoard().getChannel().getSlug(),
					post.getBoard().getBoardId(), post.getBoard().getCode(), post.getBoard().getName());
		}

		if (report.getTargetType() == ReportTargetType.COMMENT) {
			Comment comment = commentRepository.findById(report.getTargetId()).orElse(null);

			if (comment == null) {
				return ReportResponse.from(report, null, "삭제된 댓글", "대상을 찾을 수 없습니다.", null, null, null, null, null, null);
			}

			Post post = comment.getPost();

			return ReportResponse.from(report, post.getPostId(), post.getTitle(), createPreview(comment.getContent()),
					post.getBoard().getChannel().getChannelId(), post.getBoard().getChannel().getName(), post.getBoard().getChannel().getSlug(),
					post.getBoard().getBoardId(), post.getBoard().getCode(), post.getBoard().getName());
		}

		return ReportResponse.from(report, null, "-", "-", null, null, null, null, null, null);
	}

	private String createPreview(String text) {
		if (text == null || text.isBlank()) {
			return "";
		}

		String plainText = text.replaceAll("<[^>]*>", "").replace("&nbsp;", " ").trim();

		if (plainText.length() <= 60) {
			return plainText;
		}

		return plainText.substring(0, 60) + "...";
	}

	private String normalize(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}
}