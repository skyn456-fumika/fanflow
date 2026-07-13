package com.fanflow.domain.notification.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.notification.Notification;
import com.fanflow.domain.notification.NotificationType;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationResponse {

	private Long notificationId;
	private NotificationType type;
	private String message;
	private Long targetPostId;
	private Long targetCommentId;
	private boolean readStatus;
	private LocalDateTime createdAt;

	public static NotificationResponse from(Notification notification) {
		return NotificationResponse.builder().notificationId(notification.getNotificationId()).type(notification.getType())
				.message(notification.getMessage()).targetPostId(notification.getTargetPostId()).targetCommentId(notification.getTargetCommentId())
				.readStatus(notification.isReadStatus()).createdAt(notification.getCreatedAt()).build();
	}
}