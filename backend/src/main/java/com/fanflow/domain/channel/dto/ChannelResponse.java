package com.fanflow.domain.channel.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.channel.Channel;
import com.fanflow.domain.channelmember.ChannelMemberRole;
import com.fanflow.domain.channelmember.dto.ChannelMemberResponse;

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

	private long subscriberCount;
	private boolean subscribed;

	private Long ownerUserId;
	private String ownerNickname;
	private String ownerProfileImageUrl;

	private ChannelMemberRole myChannelRole;

	public static ChannelResponse from(Channel channel) {
		return from(channel, 0L, false);
	}

	public static ChannelResponse from(Channel channel, long subscriberCount, boolean subscribed) {
		return ChannelResponse.builder().channelId(channel.getChannelId()).name(channel.getName()).slug(channel.getSlug())
				.description(channel.getDescription()).profileImageUrl(channel.getProfileImageUrl()).bannerImageUrl(channel.getBannerImageUrl())
				.active(channel.isActive()).createdAt(channel.getCreatedAt()).subscriberCount(subscriberCount).subscribed(subscribed).build();
	}

	public static ChannelResponse from(Channel channel, long subscriberCount, boolean subscribed, ChannelMemberResponse owner,
			ChannelMemberRole myChannelRole) {
		return ChannelResponse.builder().channelId(channel.getChannelId()).name(channel.getName()).slug(channel.getSlug())
				.description(channel.getDescription()).profileImageUrl(channel.getProfileImageUrl()).bannerImageUrl(channel.getBannerImageUrl())
				.active(channel.isActive()).createdAt(channel.getCreatedAt()).subscriberCount(subscriberCount).subscribed(subscribed)
				.ownerUserId(owner == null ? null : owner.getUserId()).ownerNickname(owner == null ? null : owner.getNickname())
				.ownerProfileImageUrl(owner == null ? null : owner.getProfileImageUrl()).myChannelRole(myChannelRole).build();
	}
}