package com.fanflow.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TokenResponse {

	private String accessToken;
	private String tokenType;

	public static TokenResponse of(String accessToken) {
		return TokenResponse.builder().accessToken(accessToken).tokenType("Bearer").build();
	}
}