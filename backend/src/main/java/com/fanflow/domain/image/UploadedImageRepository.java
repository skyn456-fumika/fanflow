package com.fanflow.domain.image;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadedImageRepository extends JpaRepository<UploadedImage, Long> {

	List<UploadedImage> findByImageUrlIn(Collection<String> imageUrls);

	List<UploadedImage> findByUsedFalseAndCreatedAtBefore(LocalDateTime createdAt);
}