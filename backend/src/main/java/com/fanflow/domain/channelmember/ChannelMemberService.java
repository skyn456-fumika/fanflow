package com.fanflow.domain.channelmember;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.channel.Channel;
import com.fanflow.domain.channel.ChannelRepository;
import com.fanflow.domain.channelmember.dto.ChannelMemberResponse;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelMemberService {

	private final ChannelMemberRepository channelMemberRepository;
	private final ChannelRepository channelRepository;
	private final UserRepository userRepository;

	public List<ChannelMemberResponse> getChannelMembers(Long channelId) {
		getChannel(channelId);

		return channelMemberRepository.findMembersByChannelId(channelId).stream().map(ChannelMemberResponse::from).toList();
	}

	@Transactional
	public ChannelMemberResponse assignOwner(Long channelId, Long userId) {
		Channel channel = getChannel(channelId);
		User user = getActiveUser(userId);

		if (channelMemberRepository.existsByChannel_ChannelIdAndRole(channelId, ChannelMemberRole.OWNER)) {
			throw new BusinessException(ErrorCode.CHANNEL_OWNER_ALREADY_EXISTS);
		}

		if (channelMemberRepository.existsByChannel_ChannelIdAndUser_UserId(channelId, userId)) {
			throw new BusinessException(ErrorCode.CHANNEL_MEMBER_ALREADY_EXISTS);
		}

		ChannelMember channelMember = ChannelMember.builder().channel(channel).user(user).role(ChannelMemberRole.OWNER).build();

		return ChannelMemberResponse.from(channelMemberRepository.save(channelMember));
	}

	@Transactional
	public void removeOwner(Long channelId, Long userId) {
		ChannelMember channelMember = channelMemberRepository.findByChannel_ChannelIdAndUser_UserId(channelId, userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_MEMBER_NOT_FOUND));

		if (channelMember.getRole() != ChannelMemberRole.OWNER) {
			throw new BusinessException(ErrorCode.CHANNEL_MEMBER_NOT_FOUND);
		}

		channelMemberRepository.delete(channelMember);
	}

	public ChannelMemberResponse getOwner(Long channelId) {
		return channelMemberRepository.findByChannel_ChannelIdAndRole(channelId, ChannelMemberRole.OWNER).map(ChannelMemberResponse::from)
				.orElse(null);
	}

	private Channel getChannel(Long channelId) {
		return channelRepository.findById(channelId).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));
	}

	private User getActiveUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
		}

		return user;
	}
}