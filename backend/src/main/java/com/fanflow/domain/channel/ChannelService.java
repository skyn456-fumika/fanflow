package com.fanflow.domain.channel;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fanflow.domain.board.BoardService;
import com.fanflow.domain.channel.dto.ChannelCreateRequest;
import com.fanflow.domain.channel.dto.ChannelResponse;
import com.fanflow.domain.channel.dto.ChannelUpdateRequest;
import com.fanflow.domain.image.ImageUploadService;
import com.fanflow.domain.image.dto.ImageUploadResponse;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

	private final ChannelRepository channelRepository;

	private final BoardService boardService;
	private final ImageUploadService imageUploadService;

	public List<ChannelResponse> getChannels() {
		return channelRepository.findByActiveTrueOrderByNameAsc().stream().map(ChannelResponse::from).toList();
	}

	public ChannelResponse getChannel(String slug) {
		Channel channel = channelRepository.findBySlug(slug).orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));

		if (!channel.isActive()) {
			throw new BusinessException(ErrorCode.CHANNEL_NOT_ACTIVE);
		}

		return ChannelResponse.from(channel);
	}

	public List<ChannelResponse> getAdminChannels() {
		return channelRepository.findAllByOrderByCreatedAtDesc().stream().map(ChannelResponse::from).toList();
	}

	@Transactional
	public ChannelResponse createChannel(ChannelCreateRequest request) {
		String slug = normalizeSlug(request.getSlug());

		if (channelRepository.existsBySlug(slug)) {
			throw new BusinessException(ErrorCode.CHANNEL_SLUG_DUPLICATED);
		}

		Channel channel = Channel.builder().name(request.getName().trim()).slug(slug).description(normalizeNullable(request.getDescription()))
				.profileImageUrl(normalizeNullable(request.getProfileImageUrl())).bannerImageUrl(normalizeNullable(request.getBannerImageUrl()))
				.active(true).build();

		Channel savedChannel = channelRepository.save(channel);

		boardService.createDefaultBoards(savedChannel);

		return ChannelResponse.from(savedChannel);
	}

	@Transactional
	public ChannelResponse updateChannel(Long channelId, ChannelUpdateRequest request) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		String slug = normalizeSlug(request.getSlug());

		if (!channel.getSlug().equals(slug) && channelRepository.existsBySlug(slug)) {
			throw new BusinessException(ErrorCode.CHANNEL_SLUG_DUPLICATED);
		}

		channel.update(request.getName().trim(), slug, normalizeNullable(request.getDescription()), normalizeNullable(request.getProfileImageUrl()),
				normalizeNullable(request.getBannerImageUrl()));

		return ChannelResponse.from(channel);
	}

	@Transactional
	public void activateChannel(Long channelId) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		channel.activate();
	}

	@Transactional
	public void deactivateChannel(Long channelId) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		channel.deactivate();
	}

	private String normalizeSlug(String slug) {
		if (slug == null) {
			return null;
		}

		return slug.trim().toLowerCase();
	}

	private String normalizeNullable(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}

	@Transactional
	public ChannelResponse uploadProfileImage(Long channelId, MultipartFile file) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		String oldImageUrl = channel.getProfileImageUrl();

		ImageUploadResponse uploadResponse = imageUploadService.uploadChannelProfileImage(file);

		channel.updateProfileImageUrl(uploadResponse.getImageUrl());

		if (oldImageUrl != null && !oldImageUrl.equals(uploadResponse.getImageUrl())) {
			imageUploadService.deleteImageByUrl(oldImageUrl);
		}

		return ChannelResponse.from(channel);
	}

	@Transactional
	public ChannelResponse uploadBannerImage(Long channelId, MultipartFile file) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		String oldImageUrl = channel.getBannerImageUrl();

		ImageUploadResponse uploadResponse = imageUploadService.uploadChannelBannerImage(file);

		channel.updateBannerImageUrl(uploadResponse.getImageUrl());

		if (oldImageUrl != null && !oldImageUrl.equals(uploadResponse.getImageUrl())) {
			imageUploadService.deleteImageByUrl(oldImageUrl);
		}

		return ChannelResponse.from(channel);
	}
}