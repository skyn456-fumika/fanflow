package com.fanflow.domain.feed;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FeedController {

	private final FeedService feedService;

	@GetMapping("/api/feed/subscriptions")
	public ApiResponse<PageResponse<PostListResponse>> getSubscriptionFeed(@CurrentUser CustomUserDetails userDetails,
			@RequestParam(required = false) String channelSlug, @RequestParam(required = false) String boardCode,
			@RequestParam(required = false) String keyword, @RequestParam(defaultValue = "latest") String sort,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<PostListResponse> response = feedService.getSubscriptionFeed(userDetails.getUserId(), channelSlug, boardCode, keyword, sort,
				page, size);

		return ApiResponse.success("구독 피드 조회에 성공했습니다.", response);
	}
}