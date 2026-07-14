package com.fanflow.domain.channel.dto;

import com.fanflow.domain.channel.ChannelSubscription;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MySubscribedChannelResponse {

	private Long channelId;
	private String name;
	private String slug;
	private String description;
	private String profileImageUrl;
	private String bannerImageUrl;

	private long subscriberCount;
	private boolean subscribed;
	private boolean notificationEnabled;

	public static MySubscribedChannelResponse from(ChannelSubscription subscription, long subscriberCount) {
		return MySubscribedChannelResponse.builder().channelId(subscription.getChannel().getChannelId()).name(subscription.getChannel().getName())
				.slug(subscription.getChannel().getSlug()).description(subscription.getChannel().getDescription())
				.profileImageUrl(subscription.getChannel().getProfileImageUrl()).bannerImageUrl(subscription.getChannel().getBannerImageUrl())
				.subscriberCount(subscriberCount).subscribed(true).notificationEnabled(subscription.isNotificationEnabled()).build();
	}
}