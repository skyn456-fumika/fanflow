package com.fanflow.domain.bookmark;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.bookmark.dto.BookmarkStatusResponse;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class PostBookmarkController {

	private final PostBookmarkService postBookmarkService;

	@PostMapping("/api/posts/{postId}/bookmark")
	public ApiResponse<BookmarkStatusResponse> addBookmark(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails) {
		BookmarkStatusResponse response = postBookmarkService.addBookmark(postId, userDetails.getUserId());

		return ApiResponse.success("게시글을 북마크했습니다.", response);
	}

	@DeleteMapping("/api/posts/{postId}/bookmark")
	public ApiResponse<BookmarkStatusResponse> removeBookmark(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails) {
		BookmarkStatusResponse response = postBookmarkService.removeBookmark(postId, userDetails.getUserId());

		return ApiResponse.success("게시글 북마크를 해제했습니다.", response);
	}

	@GetMapping("/api/posts/{postId}/bookmark")
	public ApiResponse<BookmarkStatusResponse> getBookmarkStatus(@PathVariable Long postId, @CurrentUser CustomUserDetails userDetails) {
		BookmarkStatusResponse response = postBookmarkService.getBookmarkStatus(postId, userDetails.getUserId());

		return ApiResponse.success("게시글 북마크 상태 조회에 성공했습니다.", response);
	}

	@GetMapping("/api/users/me/bookmarks")
	public ApiResponse<PageResponse<PostListResponse>> getMyBookmarks(@CurrentUser CustomUserDetails userDetails,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<PostListResponse> response = postBookmarkService.getMyBookmarks(userDetails.getUserId(), page, size);

		return ApiResponse.success("북마크한 게시글 조회에 성공했습니다.", response);
	}
}