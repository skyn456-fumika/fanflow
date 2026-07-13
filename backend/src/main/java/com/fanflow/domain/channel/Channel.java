package com.fanflow.domain.channel;

import com.fanflow.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "channels")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Channel extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long channelId;

	@Column(nullable = false, length = 100)
	private String name;

	@Column(nullable = false, unique = true, length = 100)
	private String slug;

	@Column(length = 500)
	private String description;

	@Column(length = 500)
	private String profileImageUrl;

	@Column(length = 500)
	private String bannerImageUrl;

	@Column(nullable = false)
	private boolean active;

	@Builder
	public Channel(String name, String slug, String description, String profileImageUrl, String bannerImageUrl, boolean active) {
		this.name = name;
		this.slug = slug;
		this.description = description;
		this.profileImageUrl = profileImageUrl;
		this.bannerImageUrl = bannerImageUrl;
		this.active = active;
	}

	public void update(String name, String slug, String description, String profileImageUrl, String bannerImageUrl) {
		this.name = name;
		this.slug = slug;
		this.description = description;
		this.profileImageUrl = profileImageUrl;
		this.bannerImageUrl = bannerImageUrl;
	}

	public void activate() {
		this.active = true;
	}

	public void deactivate() {
		this.active = false;
	}
}