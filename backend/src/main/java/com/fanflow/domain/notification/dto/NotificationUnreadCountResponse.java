package com.fanflow.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationUnreadCountResponse {

	private long unreadCount;
}