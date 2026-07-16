package com.fanflow.domain.channelmember;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.channel.Channel;
import com.fanflow.domain.channel.ChannelRepository;
import com.fanflow.domain.channelmember.dto.ChannelManagerCandidateResponse;
import com.fanflow.domain.channelmember.dto.ChannelMemberResponse;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.domain.user.UserStatus;
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

	@Transactional
	public ChannelMemberResponse assignManager(Long channelId, Long userId) {
		Channel channel = getChannel(channelId);
		User user = getActiveUser(userId);

		if (channelMemberRepository.existsByChannel_ChannelIdAndUser_UserId(channelId, userId)) {
			throw new BusinessException(ErrorCode.CHANNEL_MEMBER_ALREADY_EXISTS);
		}

		ChannelMember channelMember = ChannelMember.builder().channel(channel).user(user).role(ChannelMemberRole.MANAGER).build();

		return ChannelMemberResponse.from(channelMemberRepository.save(channelMember));
	}

	@Transactional
	public void removeManager(Long channelId, Long userId) {
		ChannelMember manager = channelMemberRepository.findByChannel_ChannelIdAndUser_UserIdAndRole(channelId, userId, ChannelMemberRole.MANAGER)
				.orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_MANAGER_NOT_FOUND));

		channelMemberRepository.delete(manager);
	}

	public ChannelMemberRole getUserRole(Long channelId, Long userId) {
		if (userId == null) {
			return null;
		}

		return channelMemberRepository.findByChannel_ChannelIdAndUser_UserId(channelId, userId).map(ChannelMember::getRole).orElse(null);
	}

	public boolean isOwner(Long channelId, Long userId) {
		if (channelId == null || userId == null) {
			return false;
		}

		return channelMemberRepository.existsByChannel_ChannelIdAndUser_UserIdAndRole(channelId, userId, ChannelMemberRole.OWNER);
	}

	public void validateOwner(Long channelId, Long userId) {
		if (!isOwner(channelId, userId)) {
			throw new BusinessException(ErrorCode.CHANNEL_MANAGE_FORBIDDEN);
		}
	}

	public Channel getActiveChannel(String slug) {
		Channel channel = channelRepository.findBySlug(slug).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		if (!channel.isActive()) {
			throw new BusinessException(ErrorCode.CHANNEL_NOT_ACTIVE);
		}

		return channel;
	}

	public List<ChannelManagerCandidateResponse> searchManagerCandidates(Long channelId, Long ownerUserId, String keyword) {
		validateOwner(channelId, ownerUserId);

		String normalizedKeyword = keyword == null ? "" : keyword.trim();

		if (normalizedKeyword.isBlank()) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}

		Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("nickname")));

		return userRepository.searchAdminUsers(UserStatus.ACTIVE, normalizedKeyword, pageable).stream()
				.filter(user -> !channelMemberRepository.existsByChannel_ChannelIdAndUser_UserId(channelId, user.getUserId()))
				.map(ChannelManagerCandidateResponse::from).toList();
	}

	public boolean canModerate(Long channelId, Long userId) {
		if (channelId == null || userId == null) {
			return false;
		}

		ChannelMemberRole role = getUserRole(channelId, userId);

		return role == ChannelMemberRole.OWNER || role == ChannelMemberRole.MANAGER;
	}

	public void validateModerator(Long channelId, Long userId) {
		if (!canModerate(channelId, userId)) {
			throw new BusinessException(ErrorCode.CHANNEL_MODERATE_FORBIDDEN);
		}
	}
}