package com.fanflow.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProfileImageUpdateResponse {

	private String profileImageUrl;
}