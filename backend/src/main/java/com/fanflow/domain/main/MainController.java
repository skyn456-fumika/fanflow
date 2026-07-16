package com.fanflow.domain.main;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MainController {

	private final MainService mainService;

	@GetMapping("/api/main")
	public ApiResponse<MainResponse> getMain(Authentication authentication) {
		Long viewerId = getViewerId(authentication);

		MainResponse response = mainService.getMain(viewerId);

		return ApiResponse.success("메인 페이지 조회에 성공했습니다.", response);
	}

	private Long getViewerId(Authentication authentication) {
		if (authentication == null || !authentication.isAuthenticated()
				|| !(authentication.getPrincipal() instanceof CustomUserDetails userDetails)) {
			return null;
		}

		return userDetails.getUserId();
	}
}