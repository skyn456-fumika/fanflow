package com.fanflow.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AdminPostController {

	private final AdminPostService adminPostService;

	@GetMapping("/api/admin/posts")
	public ApiResponse<PageResponse<PostListResponse>> getPosts(@RequestParam(required = false) String boardCode,
			@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<PostListResponse> response = adminPostService.getPosts(boardCode, keyword, page, size);

		return ApiResponse.success("관리자 게시글 목록 조회에 성공했습니다.", response);
	}

	@PatchMapping("/api/admin/posts/{postId}/blind")
	public ApiResponse<Void> blindPost(@PathVariable Long postId) {
		adminPostService.blindPost(postId);
		return ApiResponse.success("게시글을 블라인드 처리했습니다.");
	}

	@PatchMapping("/api/admin/posts/{postId}/unblind")
	public ApiResponse<Void> unblindPost(@PathVariable Long postId) {
		adminPostService.unblindPost(postId);
		return ApiResponse.success("게시글 블라인드를 해제했습니다.");
	}
}