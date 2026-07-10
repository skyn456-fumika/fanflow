package com.fanflow.domain.post;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.post.dto.PostCreateRequest;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.domain.post.dto.PostResponse;
import com.fanflow.domain.post.dto.PostUpdateRequest;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostController {

	private final PostService postService;

	@PostMapping("/api/posts")
	public ApiResponse<PostResponse> createPost(@CurrentUser CustomUserDetails userDetails, @Valid @RequestBody PostCreateRequest request) {
		PostResponse response = postService.createPost(userDetails.getUserId(), request);
		return ApiResponse.success("게시글이 작성되었습니다.", response);
	}

	@GetMapping("/api/posts")
	public ApiResponse<PageResponse<PostListResponse>> getPosts(@RequestParam(required = false) String boardCode,
			@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<PostListResponse> response = postService.getPosts(boardCode, keyword, page, size);

		return ApiResponse.success("게시글 목록 조회에 성공했습니다.", response);
	}

	@GetMapping("/api/posts/{postId}")
	public ApiResponse<PostResponse> getPost(@PathVariable Long postId) {
		PostResponse response = postService.getPost(postId);
		return ApiResponse.success("게시글 상세 조회에 성공했습니다.", response);
	}

	@PutMapping("/api/posts/{postId}")
	public ApiResponse<PostResponse> updatePost(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails,
			@Valid @RequestBody PostUpdateRequest request) {
		PostResponse response = postService.updatePost(postId, userDetails.getUserId(), request);

		return ApiResponse.success("게시글이 수정되었습니다.", response);
	}

	@DeleteMapping("/api/posts/{postId}")
	public ApiResponse<Void> deletePost(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails) {
		postService.deletePost(postId, userDetails.getUserId());
		return ApiResponse.success("게시글이 삭제되었습니다.");
	}
}