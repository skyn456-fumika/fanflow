package com.fanflow.domain.block.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserBlockStatusResponse {

	private Long userId;
	private boolean blocked;

	public static UserBlockStatusResponse of(Long userId, boolean blocked) {
		return UserBlockStatusResponse.builder().userId(userId).blocked(blocked).build();
	}
}