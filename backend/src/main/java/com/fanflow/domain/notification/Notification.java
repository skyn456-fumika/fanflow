package com.fanflow.domain.notification;

import com.fanflow.domain.user.User;
import com.fanflow.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "notifications")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long notificationId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false)
	private User receiver;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 50)
	private NotificationType type;

	@Column(nullable = false, length = 500)
	private String message;

	private Long targetPostId;

	private Long targetCommentId;

	private Long actorUserId;

	@Column(nullable = false)
	private boolean readStatus;

	@Builder
	public Notification(User receiver, NotificationType type, String message, Long targetPostId, Long targetCommentId, Long actorUserId) {
		this.receiver = receiver;
		this.type = type;
		this.message = message;
		this.targetPostId = targetPostId;
		this.targetCommentId = targetCommentId;
		this.actorUserId = actorUserId;
		this.readStatus = false;
	}

	public void read() {
		this.readStatus = true;
	}
}