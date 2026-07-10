package com.fanflow.domain.user;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.user.dto.LoginRequest;
import com.fanflow.domain.user.dto.TokenResponse;
import com.fanflow.global.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;

	@PostMapping("/api/auth/login")
	public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		TokenResponse response = authService.login(request);
		return ApiResponse.success("로그인에 성공했습니다.", response);
	}
}