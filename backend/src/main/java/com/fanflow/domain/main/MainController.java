package com.fanflow.domain.main;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MainController {

	private final MainService mainService;

	@GetMapping("/api/main")
	public ApiResponse<MainResponse> getMain() {
		MainResponse response = mainService.getMain();

		return ApiResponse.success("메인 페이지 조회에 성공했습니다.", response);
	}
}