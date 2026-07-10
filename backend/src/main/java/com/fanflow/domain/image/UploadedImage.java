package com.fanflow.domain.image;

import java.time.LocalDateTime;

import com.fanflow.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UploadedImage extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long imageId;

	@Column(nullable = false)
	private String originalName;

	@Column(nullable = false, unique = true)
	private String storedName;

	@Column(nullable = false)
	private String imageUrl;

	@Column(nullable = false)
	private boolean used;

	private LocalDateTime usedAt;

	@Builder
	public UploadedImage(String originalName, String storedName, String imageUrl) {
		this.originalName = originalName;
		this.storedName = storedName;
		this.imageUrl = imageUrl;
		this.used = false;
	}

	public void markUsed() {
		this.used = true;
		this.usedAt = LocalDateTime.now();
	}
}