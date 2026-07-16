package com.fanflow.domain.channelmember.dto;

import com.fanflow.domain.user.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelManagerCandidateResponse {

	private Long userId;
	private String email;
	private String nickname;
	private String profileImageUrl;

	public static ChannelManagerCandidateResponse from(User user) {
		return ChannelManagerCandidateResponse.builder().userId(user.getUserId()).email(user.getEmail()).nickname(user.getNickname())
				.profileImageUrl(user.getProfileImageUrl()).build();
	}
}