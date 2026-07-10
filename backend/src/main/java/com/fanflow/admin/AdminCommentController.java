package com.fanflow.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.comment.dto.CommentResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AdminCommentController {

	private final AdminCommentService adminCommentService;

	@GetMapping("/api/admin/comments")
	public ApiResponse<PageResponse<CommentResponse>> getComments(@RequestParam(required = false) String keyword,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<CommentResponse> response = adminCommentService.getComments(keyword, page, size);

		return ApiResponse.success("관리자 댓글 목록 조회에 성공했습니다.", response);
	}

	@PatchMapping("/api/admin/comments/{commentId}/blind")
	public ApiResponse<Void> blindComment(@PathVariable Long commentId) {
		adminCommentService.blindComment(commentId);
		return ApiResponse.success("댓글을 블라인드 처리했습니다.");
	}

	@PatchMapping("/api/admin/comments/{commentId}/unblind")
	public ApiResponse<Void> unblindComment(@PathVariable Long commentId) {
		adminCommentService.unblindComment(commentId);
		return ApiResponse.success("댓글 블라인드를 해제했습니다.");
	}
}