package com.fanflow.domain.user;

import com.fanflow.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false)
	private String password;

	@Column(nullable = false, unique = true, length = 50)
	private String nickname;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserRole role;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private UserStatus status;

	@Column(length = 500)
	private String profileImageUrl;

	@Builder
	public User(String email, String password, String nickname, UserRole role, UserStatus status) {
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.role = role == null ? UserRole.USER : role;
		this.status = status == null ? UserStatus.ACTIVE : status;
	}

	public void changeNickname(String nickname) {
		this.nickname = nickname;
	}

	public void changePassword(String password) {
		this.password = password;
	}

	public void block() {
		this.status = UserStatus.BLOCKED;
	}

	public void activate() {
		this.status = UserStatus.ACTIVE;
	}

	public void delete() {
		this.status = UserStatus.DELETED;
	}

	public boolean isAdmin() {
		return this.role == UserRole.ADMIN;
	}

	public boolean isActive() {
		return this.status == UserStatus.ACTIVE;
	}

	public void updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}
}