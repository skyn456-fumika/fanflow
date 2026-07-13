package com.fanflow.domain.image;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageFileService {

	private final UploadedImageRepository uploadedImageRepository;

	@Value("${file.upload-dir}")
	private String uploadDir;

	public void deleteImagesFromHtml(String html) {
		if (html == null || html.isBlank()) {
			return;
		}

		Document document = Jsoup.parse(html);

		for (Element img : document.select("img[src]")) {
			String src = img.attr("src");
			deletePostImageBySrc(src);
		}
	}

	private void deletePostImageBySrc(String src) {
		if (src == null || src.isBlank()) {
			return;
		}

		try {
			String imagePath = extractImagePath(src);

			if (imagePath == null || !imagePath.startsWith("/uploads/posts/")) {
				return;
			}

			String fileName = imagePath.substring("/uploads/posts/".length());

			if (fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
				return;
			}

			Path postUploadPath = Paths.get(uploadDir, "posts").toAbsolutePath().normalize();
			Path targetPath = postUploadPath.resolve(fileName).normalize();

			if (!targetPath.startsWith(postUploadPath)) {
				return;
			}

			Files.deleteIfExists(targetPath);

		} catch (Exception e) {
			log.warn("게시글 이미지 파일 삭제 중 오류가 발생했습니다. src={}", src, e);
		}
	}

	private String extractImagePath(String src) {
		if (src.startsWith("/uploads/posts/")) {
			return src;
		}

		if (src.startsWith("http://") || src.startsWith("https://")) {
			URI uri = URI.create(src);
			return uri.getPath();
		}

		return null;
	}

	public Set<String> extractPostImagePaths(String html) {
		if (html == null || html.isBlank()) {
			return Set.of();
		}

		Document document = Jsoup.parse(html);

		Set<String> imagePaths = new HashSet<>();

		for (Element img : document.select("img[src]")) {
			String src = img.attr("src");
			String imagePath = extractImagePath(src);

			if (imagePath != null && imagePath.startsWith("/uploads/posts/")) {
				imagePaths.add(imagePath);
			}
		}

		return imagePaths;
	}

	public void deletePostImagesByPaths(Set<String> imagePaths) {
		if (imagePaths == null || imagePaths.isEmpty()) {
			return;
		}

		for (String imagePath : imagePaths) {
			deletePostImageByPath(imagePath);
		}
	}

	private void deletePostImageByPath(String imagePath) {
		if (imagePath == null || !imagePath.startsWith("/uploads/posts/")) {
			return;
		}

		try {
			String fileName = imagePath.substring("/uploads/posts/".length());

			if (fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
				return;
			}

			Path postUploadPath = Paths.get(uploadDir, "posts").toAbsolutePath().normalize();
			Path targetPath = postUploadPath.resolve(fileName).normalize();

			if (!targetPath.startsWith(postUploadPath)) {
				return;
			}

			Files.deleteIfExists(targetPath);

		} catch (Exception e) {
			log.warn("게시글 이미지 파일 삭제 중 오류가 발생했습니다. imagePath={}", imagePath, e);
		}
	}

	@Transactional
	public void markImagesAsUsedFromHtml(String html) {
		Set<String> imagePaths = extractPostImagePaths(html);

		if (imagePaths.isEmpty()) {
			return;
		}

		List<UploadedImage> uploadedImages = uploadedImageRepository.findByImageUrlIn(imagePaths);

		for (UploadedImage uploadedImage : uploadedImages) {
			if (!uploadedImage.isUsed()) {
				uploadedImage.markUsed();
			}
		}
	}

	public void deletePostImageByImageUrl(String imageUrl) {
		if (imageUrl == null || imageUrl.isBlank()) {
			return;
		}

		String imagePath = extractImagePath(imageUrl);
		deletePostImageByPath(imagePath);
	}

	public String extractFirstImageUrl(String html) {
		if (html == null || html.isBlank()) {
			return null;
		}

		Document document = Jsoup.parse(html);
		Element image = document.selectFirst("img[src]");

		if (image == null) {
			return null;
		}

		String src = image.attr("src");

		if (src == null || src.isBlank()) {
			return null;
		}

		return src;
	}

	public void deleteProfileImageByImageUrl(String imageUrl) {
		if (imageUrl == null || imageUrl.isBlank()) {
			return;
		}

		String imagePath = extractProfileImagePath(imageUrl);

		if (imagePath == null) {
			return;
		}

		deleteProfileImageByPath(imagePath);
	}

	private String extractProfileImagePath(String src) {
		if (src == null || src.isBlank()) {
			return null;
		}

		if (src.startsWith("/uploads/profiles/")) {
			return src;
		}

		if (src.startsWith("http://") || src.startsWith("https://")) {
			URI uri = URI.create(src);
			return uri.getPath();
		}

		return null;
	}

	private void deleteProfileImageByPath(String imagePath) {
		if (imagePath == null || !imagePath.startsWith("/uploads/profiles/")) {
			return;
		}

		try {
			String fileName = imagePath.substring("/uploads/profiles/".length());

			if (fileName.isBlank() || fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
				return;
			}

			Path profileUploadPath = Paths.get(uploadDir, "profiles").toAbsolutePath().normalize();
			Path targetPath = profileUploadPath.resolve(fileName).normalize();

			if (!targetPath.startsWith(profileUploadPath)) {
				return;
			}

			Files.deleteIfExists(targetPath);

		} catch (Exception e) {
			log.warn("프로필 이미지 파일 삭제 중 오류가 발생했습니다. imagePath={}", imagePath, e);
		}
	}
}