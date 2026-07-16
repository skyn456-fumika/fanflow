package com.fanflow.domain.channelmember.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.channelmember.ChannelMember;
import com.fanflow.domain.channelmember.ChannelMemberRole;
import com.fanflow.domain.user.UserStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelMemberResponse {

	private Long channelMemberId;
	private Long userId;
	private String email;
	private String nickname;
	private String profileImageUrl;
	private UserStatus status;
	private ChannelMemberRole role;
	private LocalDateTime createdAt;

	public static ChannelMemberResponse from(ChannelMember channelMember) {
		return ChannelMemberResponse.builder().channelMemberId(channelMember.getChannelMemberId()).userId(channelMember.getUser().getUserId())
				.email(channelMember.getUser().getEmail()).nickname(channelMember.getUser().getNickname())
				.profileImageUrl(channelMember.getUser().getProfileImageUrl()).status(channelMember.getUser().getStatus())
				.role(channelMember.getRole()).createdAt(channelMember.getCreatedAt()).build();
	}
}