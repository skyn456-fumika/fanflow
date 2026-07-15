package com.fanflow.domain.like;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.like.dto.CommentLikeResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class CommentLikeController {

	private final CommentLikeService commentLikeService;

	@PostMapping("/api/comments/{commentId}/likes")
	public ApiResponse<CommentLikeResponse> likeComment(@PathVariable Long commentId, @CurrentUser CustomUserDetails userDetails) {
		CommentLikeResponse response = commentLikeService.likeComment(commentId, userDetails.getUserId());

		return ApiResponse.success("댓글 좋아요를 눌렀습니다.", response);
	}

	@DeleteMapping("/api/comments/{commentId}/likes")
	public ApiResponse<CommentLikeResponse> unlikeComment(@PathVariable Long commentId, @CurrentUser CustomUserDetails userDetails) {
		CommentLikeResponse response = commentLikeService.unlikeComment(commentId, userDetails.getUserId());

		return ApiResponse.success("댓글 좋아요를 취소했습니다.", response);
	}

	@GetMapping("/api/comments/{commentId}/likes/me")
	public ApiResponse<CommentLikeResponse> getMyLikeStatus(@PathVariable Long commentId, @CurrentUser CustomUserDetails userDetails) {
		CommentLikeResponse response = commentLikeService.getMyLikeStatus(commentId, userDetails.getUserId());

		return ApiResponse.success("댓글 좋아요 상태 조회에 성공했습니다.", response);
	}
}