package com.fanflow.domain.image;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImageCleanupScheduler {

	private final UploadedImageRepository uploadedImageRepository;
	private final ImageFileService imageFileService;

	@Scheduled(cron = "0 * * * * *")
	public void cleanupUnusedImages() {
		LocalDateTime cutoff = LocalDateTime.now().minusMinutes(1);

		List<UploadedImage> unusedImages = uploadedImageRepository.findByUsedFalseAndCreatedAtBefore(cutoff);

		if (unusedImages.isEmpty()) {
			return;
		}

		for (UploadedImage image : unusedImages) {
			imageFileService.deletePostImageByImageUrl(image.getImageUrl());
			uploadedImageRepository.delete(image);
		}

		log.info("미사용 이미지 정리 완료 count={}", unusedImages.size());
	}
}