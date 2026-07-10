package com.fanflow.domain.image.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageUploadResponse {

	private String originalName;
	private String storedName;
	private String imageUrl;

	public static ImageUploadResponse of(String originalName, String storedName, String imageUrl) {
		return ImageUploadResponse.builder().originalName(originalName).storedName(storedName).imageUrl(imageUrl).build();
	}
}