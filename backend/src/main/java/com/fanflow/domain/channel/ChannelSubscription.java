package com.fanflow.domain.channel;

import com.fanflow.domain.user.User;
import com.fanflow.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "channel_subscriptions", uniqueConstraints = {
		@UniqueConstraint(name = "uk_channel_subscriptions_user_channel", columnNames = { "user_id", "channel_id" }) })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelSubscription extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long subscriptionId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	@Column(nullable = false)
	private boolean notificationEnabled;

	@Builder
	public ChannelSubscription(User user, Channel channel) {
		this.user = user;
		this.channel = channel;
		this.notificationEnabled = true;
	}

	public void enableNotification() {
		this.notificationEnabled = true;
	}

	public void disableNotification() {
		this.notificationEnabled = false;
	}

	public void changeNotificationEnabled(boolean notificationEnabled) {
		this.notificationEnabled = notificationEnabled;
	}
}