package com.fanflow.domain.channel.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.channel.Channel;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelResponse {

	private Long channelId;
	private String name;
	private String slug;
	private String description;
	private String profileImageUrl;
	private String bannerImageUrl;
	private boolean active;
	private LocalDateTime createdAt;

	public static ChannelResponse from(Channel channel) {
		return ChannelResponse.builder().channelId(channel.getChannelId()).name(channel.getName()).slug(channel.getSlug())
				.description(channel.getDescription()).profileImageUrl(channel.getProfileImageUrl()).bannerImageUrl(channel.getBannerImageUrl())
				.active(channel.isActive()).createdAt(channel.getCreatedAt()).build();
	}
}