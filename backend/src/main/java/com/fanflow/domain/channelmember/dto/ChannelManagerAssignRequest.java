package com.fanflow.domain.channelmember.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ChannelManagerAssignRequest {

	@NotNull
	private Long userId;
}