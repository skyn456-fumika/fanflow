package com.fanflow.domain.channel;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fanflow.domain.board.BoardRepository;
import com.fanflow.domain.board.BoardService;
import com.fanflow.domain.board.dto.BoardResponse;
import com.fanflow.domain.channel.dto.ChannelCreateRequest;
import com.fanflow.domain.channel.dto.ChannelHomeResponse;
import com.fanflow.domain.channel.dto.ChannelNotificationSettingRequest;
import com.fanflow.domain.channel.dto.ChannelResponse;
import com.fanflow.domain.channel.dto.ChannelSubscriptionStatusResponse;
import com.fanflow.domain.channel.dto.ChannelUpdateRequest;
import com.fanflow.domain.channelmember.ChannelMemberService;
import com.fanflow.domain.channelmember.dto.ChannelMemberResponse;
import com.fanflow.domain.image.ImageUploadService;
import com.fanflow.domain.image.dto.ImageUploadResponse;
import com.fanflow.domain.post.PostRepository;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChannelService {

	private final ChannelRepository channelRepository;
	private final BoardRepository boardRepository;
	private final PostRepository postRepository;
	private final ChannelSubscriptionRepository channelSubscriptionRepository;
	private final UserRepository userRepository;

	private final BoardService boardService;
	private final ImageUploadService imageUploadService;
	private final ChannelMemberService channelMemberService;

	public List<ChannelResponse> getChannels() {
		return channelRepository.findByActiveTrueOrderByNameAsc().stream().map(this::toChannelResponse).toList();
	}

	public ChannelResponse getChannel(String slug) {
		Channel channel = channelRepository.findBySlug(slug).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		if (!channel.isActive()) {
			throw new BusinessException(ErrorCode.CHANNEL_NOT_ACTIVE);
		}

		return toChannelResponse(channel);
	}

	public ChannelHomeResponse getChannelHome(String slug, Long viewerId) {
		Channel channel = channelRepository.findBySlug(slug).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		if (!channel.isActive()) {
			throw new BusinessException(ErrorCode.CHANNEL_NOT_ACTIVE);
		}

		ChannelMemberResponse owner = channelMemberService.getOwner(channel.getChannelId());

		List<BoardResponse> boards = boardRepository.findActiveBoardsByChannelSlug(slug).stream().map(BoardResponse::from).toList();

		List<PostListResponse> noticePosts = postRepository.findChannelHomeNoticePosts(slug, PageRequest.of(0, 3)).stream()
				.map(PostListResponse::from).toList();

		List<PostListResponse> popularPosts = postRepository.findChannelHomePopularPosts(slug, viewerId, PageRequest.of(0, 5)).stream()
				.map(PostListResponse::from).toList();

		List<PostListResponse> recentPosts = postRepository.findChannelHomeRecentPosts(slug, viewerId, PageRequest.of(0, 5)).stream()
				.map(PostListResponse::from).toList();

		return ChannelHomeResponse.builder().channel(toChannelResponse(channel, viewerId, owner)).boards(boards).noticePosts(noticePosts)
				.popularPosts(popularPosts).recentPosts(recentPosts).build();
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

	private ChannelResponse toChannelResponse(Channel channel) {
		long subscriberCount = channelSubscriptionRepository.countByChannel_ChannelId(channel.getChannelId());

		return ChannelResponse.from(channel, subscriberCount, false);
	}

	private ChannelResponse toChannelResponse(Channel channel, Long userId) {
		long subscriberCount = channelSubscriptionRepository.countByChannel_ChannelId(channel.getChannelId());

		boolean subscribed = userId != null && channelSubscriptionRepository.existsByUser_UserIdAndChannel_ChannelId(userId, channel.getChannelId());

		return ChannelResponse.from(channel, subscriberCount, subscribed);
	}

	@Transactional
	public ChannelSubscriptionStatusResponse subscribeChannel(String slug, Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		Channel channel = channelRepository.findBySlug(slug).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		if (!channel.isActive()) {
			throw new BusinessException(ErrorCode.CHANNEL_NOT_ACTIVE);
		}

		if (channelSubscriptionRepository.existsByUser_UserIdAndChannel_ChannelId(userId, channel.getChannelId())) {
			throw new BusinessException(ErrorCode.ALREADY_SUBSCRIBED_CHANNEL);
		}

		ChannelSubscription subscription = ChannelSubscription.builder().user(user).channel(channel).build();

		channelSubscriptionRepository.save(subscription);

		return toSubscriptionStatus(channel, userId);
	}

	@Transactional
	public ChannelSubscriptionStatusResponse unsubscribeChannel(String slug, Long userId) {
		Channel channel = channelRepository.findBySlug(slug).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		ChannelSubscription subscription = channelSubscriptionRepository.findByUser_UserIdAndChannel_ChannelId(userId, channel.getChannelId())
				.orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_SUBSCRIPTION_NOT_FOUND));

		channelSubscriptionRepository.delete(subscription);

		return toSubscriptionStatus(channel, userId);
	}

	public ChannelSubscriptionStatusResponse getSubscriptionStatus(String slug, Long userId) {
		Channel channel = channelRepository.findBySlug(slug).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		if (!channel.isActive()) {
			throw new BusinessException(ErrorCode.CHANNEL_NOT_ACTIVE);
		}

		return toSubscriptionStatus(channel, userId);
	}

	private ChannelSubscriptionStatusResponse toSubscriptionStatus(Channel channel, Long userId) {
		ChannelSubscription subscription = channelSubscriptionRepository.findByUser_UserIdAndChannel_ChannelId(userId, channel.getChannelId())
				.orElse(null);

		boolean subscribed = subscription != null;
		boolean notificationEnabled = subscription != null && subscription.isNotificationEnabled();

		long subscriberCount = channelSubscriptionRepository.countByChannel_ChannelId(channel.getChannelId());

		return ChannelSubscriptionStatusResponse.builder().channelId(channel.getChannelId()).channelSlug(channel.getSlug()).subscribed(subscribed)
				.notificationEnabled(notificationEnabled).subscriberCount(subscriberCount).build();
	}

	@Transactional
	public ChannelSubscriptionStatusResponse updateSubscriptionNotification(String slug, Long userId, ChannelNotificationSettingRequest request) {
		Channel channel = channelRepository.findBySlug(slug).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		if (!channel.isActive()) {
			throw new BusinessException(ErrorCode.CHANNEL_NOT_ACTIVE);
		}

		ChannelSubscription subscription = channelSubscriptionRepository.findByUser_UserIdAndChannel_ChannelId(userId, channel.getChannelId())
				.orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_SUBSCRIPTION_NOT_FOUND));

		subscription.changeNotificationEnabled(request.isNotificationEnabled());

		return toSubscriptionStatus(channel, userId);
	}

	private ChannelResponse toChannelResponse(Channel channel, Long userId, ChannelMemberResponse owner) {
		long subscriberCount = channelSubscriptionRepository.countByChannel_ChannelId(channel.getChannelId());

		boolean subscribed = userId != null && channelSubscriptionRepository.existsByUser_UserIdAndChannel_ChannelId(userId, channel.getChannelId());

		return ChannelResponse.from(channel, subscriberCount, subscribed, owner);
	}
}