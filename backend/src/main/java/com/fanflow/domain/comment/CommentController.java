package com.fanflow.domain.comment;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.comment.dto.CommentCreateRequest;
import com.fanflow.domain.comment.dto.CommentResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentController {

	private final CommentService commentService;

	@PostMapping("/api/posts/{postId}/comments")
	public ApiResponse<CommentResponse> createComment(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails,
			@Valid @RequestBody CommentCreateRequest request) {
		CommentResponse response = commentService.createComment(postId, userDetails.getUserId(), request);

		return ApiResponse.success("댓글이 작성되었습니다.", response);
	}

	@GetMapping("/api/posts/{postId}/comments")
	public ApiResponse<List<CommentResponse>> getComments(@PathVariable Long postId) {
		List<CommentResponse> response = commentService.getComments(postId);
		return ApiResponse.success("댓글 목록 조회에 성공했습니다.", response);
	}

	@DeleteMapping("/api/comments/{commentId}")
	public ApiResponse<Void> deleteComment(@PathVariable Long commentId, @CurrentUser CustomUserDetails userDetails) {
		commentService.deleteComment(commentId, userDetails.getUserId());
		return ApiResponse.success("댓글이 삭제되었습니다.");
	}
}