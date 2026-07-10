package com.fanflow.domain.image;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fanflow.domain.image.dto.ImageUploadResponse;
import com.fanflow.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ImageUploadController {

	private final ImageUploadService imageUploadService;

	@PostMapping("/api/posts/images")
	public ApiResponse<ImageUploadResponse> uploadPostImage(@RequestParam("file") MultipartFile file) {
		ImageUploadResponse response = imageUploadService.uploadPostImage(file);
		return ApiResponse.success("이미지 업로드에 성공했습니다.", response);
	}
}