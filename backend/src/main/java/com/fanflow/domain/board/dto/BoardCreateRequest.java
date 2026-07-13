package com.fanflow.domain.board.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BoardCreateRequest {

	@NotBlank(message = "게시판 코드는 필수입니다.")
	@Size(max = 30, message = "게시판 코드는 30자 이하로 입력해야 합니다.")
	@Pattern(regexp = "^[A-Z0-9_]+$", message = "게시판 코드는 영문 대문자, 숫자, 언더스코어만 사용할 수 있습니다.")
	private String code;

	@NotBlank(message = "게시판명은 필수입니다.")
	@Size(max = 50, message = "게시판명은 50자 이하로 입력해야 합니다.")
	private String name;

	@Size(max = 255, message = "설명은 255자 이하로 입력해야 합니다.")
	private String description;

	@Min(value = 0, message = "정렬 순서는 0 이상이어야 합니다.")
	private int sortOrder;
}