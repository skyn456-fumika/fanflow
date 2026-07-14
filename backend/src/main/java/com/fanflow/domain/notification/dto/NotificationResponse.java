package com.fanflow.domain.notification.dto;

import java.time.LocalDateTime;

import com.fanflow.domain.notification.Notification;
import com.fanflow.domain.notification.NotificationType;
import com.fanflow.domain.post.Post;

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
	private String targetPostTitle;

	private Long channelId;
	private String channelName;
	private String channelSlug;

	private Long boardId;
	private String boardCode;
	private String boardName;

	private boolean readStatus;
	private LocalDateTime createdAt;

	public static NotificationResponse from(Notification notification) {
		return from(notification, null);
	}

	public static NotificationResponse from(Notification notification, Post post) {
		return NotificationResponse.builder().notificationId(notification.getNotificationId()).type(notification.getType())
				.message(notification.getMessage()).targetPostId(notification.getTargetPostId()).targetCommentId(notification.getTargetCommentId())
				.targetPostTitle(post != null ? post.getTitle() : null).channelId(post != null ? post.getBoard().getChannel().getChannelId() : null)
				.channelName(post != null ? post.getBoard().getChannel().getName() : null)
				.channelSlug(post != null ? post.getBoard().getChannel().getSlug() : null).boardId(post != null ? post.getBoard().getBoardId() : null)
				.boardCode(post != null ? post.getBoard().getCode() : null).boardName(post != null ? post.getBoard().getName() : null)
				.readStatus(notification.isReadStatus()).createdAt(notification.getCreatedAt()).build();
	}
}