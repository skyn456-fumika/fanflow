package com.fanflow.domain.block.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.block.UserBlock;
import com.fanflow.domain.user.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BlockedUserResponse {

	private Long userId;
	private String nickname;
	private String profileImageUrl;
	private LocalDateTime blockedAt;

	public static BlockedUserResponse from(UserBlock userBlock) {
		User blocked = userBlock.getBlocked();

		return BlockedUserResponse.builder().userId(blocked.getUserId()).nickname(blocked.getNickname()).profileImageUrl(blocked.getProfileImageUrl())
				.blockedAt(userBlock.getCreatedAt()).build();
	}
}