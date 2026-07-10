package com.fanflow.domain.like;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.like.dto.PostLikeResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostLikeController {

	private final PostLikeService postLikeService;

	@PostMapping("/api/posts/{postId}/likes")
	public ApiResponse<PostLikeResponse> likePost(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails) {
		PostLikeResponse response = postLikeService.likePost(postId, userDetails.getUserId());

		return ApiResponse.success("게시글 좋아요를 눌렀습니다.", response);
	}

	@DeleteMapping("/api/posts/{postId}/likes")
	public ApiResponse<PostLikeResponse> unlikePost(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails) {
		PostLikeResponse response = postLikeService.unlikePost(postId, userDetails.getUserId());

		return ApiResponse.success("게시글 좋아요를 취소했습니다.", response);
	}

	@GetMapping("/api/posts/{postId}/likes/me")
	public ApiResponse<PostLikeResponse> getMyLikeStatus(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails) {
		PostLikeResponse response = postLikeService.getMyLikeStatus(postId, userDetails.getUserId());

		return ApiResponse.success("게시글 좋아요 상태 조회에 성공했습니다.", response);
	}
}