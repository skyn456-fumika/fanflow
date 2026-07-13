package com.fanflow.domain.channel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChannelUpdateRequest {

	@NotBlank(message = "채널명은 필수입니다.")
	@Size(max = 100, message = "채널명은 100자 이하로 입력해야 합니다.")
	private String name;

	@NotBlank(message = "채널 주소는 필수입니다.")
	@Size(max = 100, message = "채널 주소는 100자 이하로 입력해야 합니다.")
	@Pattern(regexp = "^[a-z0-9-]+$", message = "채널 주소는 영문 소문자, 숫자, 하이픈만 사용할 수 있습니다.")
	private String slug;

	@Size(max = 500, message = "설명은 500자 이하로 입력해야 합니다.")
	private String description;

	@Size(max = 500, message = "프로필 이미지 URL은 500자 이하로 입력해야 합니다.")
	private String profileImageUrl;

	@Size(max = 500, message = "배너 이미지 URL은 500자 이하로 입력해야 합니다.")
	private String bannerImageUrl;
}