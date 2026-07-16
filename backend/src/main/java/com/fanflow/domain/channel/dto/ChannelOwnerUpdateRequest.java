package com.fanflow.domain.channel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChannelOwnerUpdateRequest {

	@NotBlank(message = "채널명은 필수입니다.")
	@Size(max = 100, message = "채널명은 100자 이하로 입력해야 합니다.")
	private String name;

	@Size(max = 500, message = "설명은 500자 이하로 입력해야 합니다.")
	private String description;
}