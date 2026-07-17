package com.fanflow.domain.channelmember;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.comment.CommentService;
import com.fanflow.domain.comment.dto.BlindCommentResponse;
import com.fanflow.domain.comment.dto.CommentResponse;
import com.fanflow.domain.post.PostService;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.domain.post.dto.PostResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChannelModerationController {

	private final PostService postService;
	private final CommentService commentService;

	@PatchMapping("/api/channel-management/posts/{postId}/blind")
	public ApiResponse<PostResponse> blindPost(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("게시글이 블라인드 처리되었습니다.", postService.blindPostByModerator(postId, userDetails.getUserId()));
	}

	@PatchMapping("/api/channel-management/posts/{postId}/unblind")
	public ApiResponse<PostResponse> unblindPost(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("게시글 블라인드가 해제되었습니다.", postService.unblindPostByModerator(postId, userDetails.getUserId()));
	}

	@PatchMapping("/api/channel-management/comments/{commentId}/blind")
	public ApiResponse<CommentResponse> blindComment(@PathVariable Long commentId, @CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("댓글이 블라인드 처리되었습니다.", commentService.blindCommentByModerator(commentId, userDetails.getUserId()));
	}

	@PatchMapping("/api/channel-management/comments/{commentId}/unblind")
	public ApiResponse<CommentResponse> unblindComment(@PathVariable Long commentId, @CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("댓글 블라인드가 해제되었습니다.", commentService.unblindCommentByModerator(commentId, userDetails.getUserId()));
	}

	@GetMapping("/api/channel-management/channels/{slug}/blind-posts")
	public ApiResponse<List<PostListResponse>> getBlindPosts(@PathVariable String slug, @CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("블라인드 게시글 목록 조회에 성공했습니다.", postService.getBlindPostsByModerator(slug, userDetails.getUserId()));
	}

	@GetMapping("/api/channel-management/channels/{slug}/blind-comments")
	public ApiResponse<List<BlindCommentResponse>> getBlindComments(@PathVariable String slug, @CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("블라인드 댓글 목록 조회에 성공했습니다.", commentService.getBlindCommentsByModerator(slug, userDetails.getUserId()));
	}
}