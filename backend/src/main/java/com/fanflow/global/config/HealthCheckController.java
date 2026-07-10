package com.fanflow.global.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.global.response.ApiResponse;

@RestController
public class HealthCheckController {

	@GetMapping("/")
	public ApiResponse<String> home() {
		return ApiResponse.success("FanFlow server is running", "OK");
	}

	@GetMapping("/api/health")
	public ApiResponse<String> health() {
		return ApiResponse.success("Health check success", "OK");
	}
}