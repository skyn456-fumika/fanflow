package com.fanflow.admin.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRole;
import com.fanflow.domain.user.UserStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AdminUserResponse {

	private Long userId;
	private String email;
	private String nickname;
	private String profileImageUrl;
	private UserRole role;
	private UserStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public static AdminUserResponse from(User user) {
		return AdminUserResponse.builder().userId(user.getUserId()).email(user.getEmail()).nickname(user.getNickname())
				.profileImageUrl(user.getProfileImageUrl()).role(user.getRole()).status(user.getStatus()).createdAt(user.getCreatedAt())
				.updatedAt(user.getUpdatedAt()).build();
	}
}