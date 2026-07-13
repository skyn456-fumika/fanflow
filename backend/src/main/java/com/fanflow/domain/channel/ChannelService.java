package com.fanflow.domain.channel;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.channel.dto.ChannelCreateRequest;
import com.fanflow.domain.channel.dto.ChannelResponse;
import com.fanflow.domain.channel.dto.ChannelUpdateRequest;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

	private final ChannelRepository channelRepository;

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
}