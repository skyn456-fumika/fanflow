package com.fanflow.domain.channel.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChannelSubscriptionStatusResponse {

	private Long channelId;
	private String channelSlug;
	private boolean subscribed;
	private long subscriberCount;
}