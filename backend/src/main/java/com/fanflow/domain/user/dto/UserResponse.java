package com.fanflow.domain.user.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRole;
import com.fanflow.domain.user.UserStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

	private Long userId;
	private String email;
	private String nickname;
	private UserRole role;
	private UserStatus status;
	private LocalDateTime createdAt;
	private String profileImageUrl;

	public static UserResponse from(User user) {
		return UserResponse.builder().userId(user.getUserId()).email(user.getEmail()).nickname(user.getNickname()).role(user.getRole())
				.status(user.getStatus()).createdAt(user.getCreatedAt()).profileImageUrl(user.getProfileImageUrl()).build();
	}
}