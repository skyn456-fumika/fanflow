package com.fanflow.domain.block;

import com.fanflow.domain.user.User;
import com.fanflow.global.entity.BaseEntity;

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
@Table(name = "user_blocks", uniqueConstraints = {
		@UniqueConstraint(name = "uk_user_blocks_blocker_blocked", columnNames = { "blocker_id", "blocked_id" }) })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBlock extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userBlockId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocker_id", nullable = false)
	private User blocker;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "blocked_id", nullable = false)
	private User blocked;

	@Builder
	public UserBlock(User blocker, User blocked) {
		this.blocker = blocker;
		this.blocked = blocked;
	}
}