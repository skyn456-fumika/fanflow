package com.fanflow.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.comment.Comment;
import com.fanflow.domain.comment.CommentRepository;
import com.fanflow.domain.comment.dto.CommentResponse;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminCommentService {

	private final CommentRepository commentRepository;

	public PageResponse<CommentResponse> getComments(String keyword, int page, int size) {
		page = Math.max(page, 0);

		if (size < 1) {
			size = 10;
		}

		if (size > 50) {
			size = 50;
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

		Page<CommentResponse> comments = commentRepository.searchAdminComments(normalize(keyword), pageable).map(CommentResponse::from);

		return PageResponse.from(comments);
	}

	@Transactional
	public void blindComment(Long commentId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (comment.isDeleted()) {
			throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
		}

		if (!comment.isBlind()) {
			comment.blind();
			comment.getPost().decreaseCommentCount();
		}
	}

	@Transactional
	public void unblindComment(Long commentId) {
		Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

		if (comment.isDeleted()) {
			throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND);
		}

		if (comment.isBlind()) {
			comment.unblind();
			comment.getPost().increaseCommentCount();
		}
	}

	private String normalize(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}
}