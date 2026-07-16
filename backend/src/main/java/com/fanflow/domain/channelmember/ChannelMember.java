package com.fanflow.domain.channelmember;

import com.fanflow.domain.channel.Channel;
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
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "channel_members", uniqueConstraints = {
		@UniqueConstraint(name = "uk_channel_members_channel_user", columnNames = { "channel_id", "user_id" }) })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChannelMember extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long channelMemberId;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ChannelMemberRole role;

	@Builder
	public ChannelMember(Channel channel, User user, ChannelMemberRole role) {
		this.channel = channel;
		this.user = user;
		this.role = role;
	}
}