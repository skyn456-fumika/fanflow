package com.fanflow.domain.user.dto;

import com.fanflow.domain.channel.Channel;
import com.fanflow.domain.channelmember.ChannelMember;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserOwnedChannelResponse {

	private Long channelId;
	private String name;
	private String slug;
	private String description;
	private String profileImageUrl;
	private String bannerImageUrl;
	private boolean active;

	public static UserOwnedChannelResponse from(ChannelMember channelMember) {
		Channel channel = channelMember.getChannel();

		return UserOwnedChannelResponse.builder().channelId(channel.getChannelId()).name(channel.getName()).slug(channel.getSlug())
				.description(channel.getDescription()).profileImageUrl(channel.getProfileImageUrl()).bannerImageUrl(channel.getBannerImageUrl())
				.active(channel.isActive()).build();
	}
}