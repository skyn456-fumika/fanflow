package com.fanflow.domain.user.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fanflow.domain.user.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPublicProfileResponse {

	private Long userId;
	private String nickname;
	private String profileImageUrl;
	private LocalDateTime createdAt;

	private long postCount;
	private long commentCount;

	private boolean streamer;
	private List<UserOwnedChannelResponse> ownedChannels;

	public static UserPublicProfileResponse from(User user, long postCount, long commentCount, List<UserOwnedChannelResponse> ownedChannels) {
		List<UserOwnedChannelResponse> safeOwnedChannels = ownedChannels == null ? List.of() : ownedChannels;

		return UserPublicProfileResponse.builder().userId(user.getUserId()).nickname(user.getNickname()).profileImageUrl(user.getProfileImageUrl())
				.createdAt(user.getCreatedAt()).postCount(postCount).commentCount(commentCount).streamer(!safeOwnedChannels.isEmpty())
				.ownedChannels(safeOwnedChannels).build();
	}
}