package com.fanflow.domain.post.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

	@NotBlank(message = "게시판 코드는 필수입니다.")
	private String boardCode;

	@NotBlank(message = "제목은 필수입니다.")
	@Size(max = 200, message = "제목은 200자 이하로 입력해야 합니다.")
	private String title;

	@NotBlank(message = "내용은 필수입니다.")
	private String content;
}