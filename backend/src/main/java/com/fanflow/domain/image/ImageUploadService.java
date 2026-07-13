package com.fanflow.domain.image;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fanflow.domain.image.dto.ImageUploadResponse;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageUploadService {

	private final UploadedImageRepository uploadedImageRepository;

	private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

	private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");

	private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");

	@Value("${file.upload-dir}")
	private String uploadDir;

	public ImageUploadResponse uploadPostImage(MultipartFile file) {
		validateImage(file);

		String originalName = file.getOriginalFilename();
		String extension = getExtension(originalName);
		String storedName = UUID.randomUUID() + "." + extension;

		try {
			Path uploadPath = Paths.get(uploadDir, "posts").toAbsolutePath().normalize();
			Files.createDirectories(uploadPath);

			Path targetPath = uploadPath.resolve(storedName).normalize();

			file.transferTo(targetPath.toFile());

			String imageUrl = "/uploads/posts/" + storedName;

			UploadedImage uploadedImage = UploadedImage.builder().originalName(originalName).storedName(storedName).imageUrl(imageUrl).build();

			uploadedImageRepository.save(uploadedImage);

			return ImageUploadResponse.builder().originalName(originalName).storedName(storedName).imageUrl(imageUrl).build();

		} catch (IOException e) {
			throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
		}
	}

	private void validateImage(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BusinessException(ErrorCode.IMAGE_FILE_EMPTY);
		}

		if (file.getSize() > MAX_IMAGE_SIZE) {
			throw new BusinessException(ErrorCode.IMAGE_FILE_TOO_LARGE);
		}

		String originalName = file.getOriginalFilename();
		String extension = getExtension(originalName);

		if (!ALLOWED_EXTENSIONS.contains(extension)) {
			throw new BusinessException(ErrorCode.IMAGE_EXTENSION_NOT_ALLOWED);
		}

		String contentType = file.getContentType();

		if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
			throw new BusinessException(ErrorCode.IMAGE_CONTENT_TYPE_NOT_ALLOWED);
		}
	}

	private String getExtension(String filename) {
		if (filename == null || filename.isBlank()) {
			throw new BusinessException(ErrorCode.IMAGE_EXTENSION_NOT_ALLOWED);
		}

		int dotIndex = filename.lastIndexOf(".");

		if (dotIndex == -1 || dotIndex == filename.length() - 1) {
			throw new BusinessException(ErrorCode.IMAGE_EXTENSION_NOT_ALLOWED);
		}

		return filename.substring(dotIndex + 1).toLowerCase();
	}

	public ImageUploadResponse uploadProfileImage(MultipartFile file) {
		validateImage(file);

		String originalFilename = file.getOriginalFilename();
		String extension = getExtension(originalFilename);
		String savedFileName = UUID.randomUUID() + "." + extension;

		try {
			Path uploadPath = Paths.get(uploadDir, "profiles").toAbsolutePath().normalize();
			Files.createDirectories(uploadPath);

			Path targetPath = uploadPath.resolve(savedFileName).normalize();

			if (!targetPath.startsWith(uploadPath)) {
				throw new BusinessException(ErrorCode.FILE_UPLOAD_FAILED);
			}

			file.transferTo(targetPath.toFile());

			String imageUrl = "/uploads/profiles/" + savedFileName;

			return ImageUploadResponse.builder().imageUrl(imageUrl).build();

		} catch (IOException e) {
			throw new BusinessException(ErrorCode.IMAGE_UPLOAD_FAILED);
		}
	}
}