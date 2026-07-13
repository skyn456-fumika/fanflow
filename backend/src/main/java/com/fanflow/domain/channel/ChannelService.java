package com.fanflow.domain.channel;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.channel.dto.ChannelResponse;
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
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}

		return ChannelResponse.from(channel);
	}
}