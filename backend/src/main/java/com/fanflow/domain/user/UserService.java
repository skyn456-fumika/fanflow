package com.fanflow.domain.user;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fanflow.domain.channel.ChannelSubscriptionRepository;
import com.fanflow.domain.channel.dto.ChannelResponse;
import com.fanflow.domain.comment.CommentRepository;
import com.fanflow.domain.comment.dto.CommentResponse;
import com.fanflow.domain.image.ImageFileService;
import com.fanflow.domain.image.ImageUploadService;
import com.fanflow.domain.image.dto.ImageUploadResponse;
import com.fanflow.domain.like.PostLikeRepository;
import com.fanflow.domain.post.PostRepository;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.domain.user.dto.NicknameUpdateRequest;
import com.fanflow.domain.user.dto.PasswordUpdateRequest;
import com.fanflow.domain.user.dto.ProfileImageUpdateResponse;
import com.fanflow.domain.user.dto.SignupRequest;
import com.fanflow.domain.user.dto.UserDeleteRequest;
import com.fanflow.domain.user.dto.UserPublicProfileResponse;
import com.fanflow.domain.user.dto.UserResponse;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PostRepository postRepository;
	private final CommentRepository commentRepository;
	private final PostLikeRepository postLikeRepository;
	private final ChannelSubscriptionRepository channelSubscriptionRepository;

	private final ImageUploadService imageUploadService;
	private final ImageFileService imageFileService;

	@Transactional
	public UserResponse signup(SignupRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}

		if (userRepository.existsByNickname(request.getNickname())) {
			throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}

		User user = User.builder().email(request.getEmail()).password(passwordEncoder.encode(request.getPassword())).nickname(request.getNickname())
				.role(UserRole.USER).status(UserStatus.ACTIVE).build();

		User savedUser = userRepository.save(user);

		return UserResponse.from(savedUser);
	}

	@Transactional
	public UserResponse updateNickname(Long userId, NicknameUpdateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		String nickname = request.getNickname().trim();

		if (!user.getNickname().equals(nickname) && userRepository.existsByNickname(nickname)) {
			throw new BusinessException(ErrorCode.NICKNAME_ALREADY_EXISTS);
		}

		user.changeNickname(nickname);

		return UserResponse.from(user);
	}

	@Transactional
	public void updatePassword(Long userId, PasswordUpdateRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
			throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCHED);
		}

		String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());

		user.changePassword(encodedNewPassword);
	}

	public PageResponse<PostListResponse> getMyPosts(Long userId, int page, int size) {
		Pageable pageable = createPageable(page, size);

		Page<PostListResponse> posts = postRepository.findMyPosts(userId, pageable).map(PostListResponse::from);

		return PageResponse.from(posts);
	}

	public PageResponse<CommentResponse> getMyComments(Long userId, int page, int size) {
		Pageable pageable = createPageable(page, size);

		Page<CommentResponse> comments = commentRepository.findMyComments(userId, pageable).map(CommentResponse::from);

		return PageResponse.from(comments);
	}

	public PageResponse<PostListResponse> getMyLikedPosts(Long userId, int page, int size) {
		Pageable pageable = createPageable(page, size);

		Page<PostListResponse> posts = postLikeRepository.findMyPostLikes(userId, pageable)
				.map(postLike -> PostListResponse.from(postLike.getPost()));

		return PageResponse.from(posts);
	}

	@Transactional
	public void deleteMe(Long userId, UserDeleteRequest request) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BusinessException(ErrorCode.PASSWORD_NOT_MATCHED);
		}

		user.delete();
	}

	// 메서드 구간
	private Pageable createPageable(int page, int size) {
		if (page < 0) {
			page = 0;
		}

		if (size < 1) {
			size = 10;
		}

		if (size > 50) {
			size = 50;
		}

		return PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
	}

	@Transactional
	public ProfileImageUpdateResponse updateProfileImage(Long userId, MultipartFile file) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		String oldProfileImageUrl = user.getProfileImageUrl();

		ImageUploadResponse uploadResponse = imageUploadService.uploadProfileImage(file);

		user.updateProfileImageUrl(uploadResponse.getImageUrl());

		if (oldProfileImageUrl != null && !oldProfileImageUrl.isBlank()) {
			imageFileService.deleteProfileImageByImageUrl(oldProfileImageUrl);
		}

		return ProfileImageUpdateResponse.builder().profileImageUrl(user.getProfileImageUrl()).build();
	}

	public UserPublicProfileResponse getPublicProfile(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}

		long postCount = postRepository.countByWriter_UserIdAndDeletedFalseAndBlindFalse(userId);
		long commentCount = commentRepository.countPublicCommentsByUserId(userId);

		return UserPublicProfileResponse.from(user, postCount, commentCount);
	}

	public PageResponse<PostListResponse> getPublicPosts(Long userId, int page, int size) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}

		Pageable pageable = createPageable(page, size);

		Page<PostListResponse> posts = postRepository.findPublicPostsByUserId(userId, pageable).map(PostListResponse::from);

		return PageResponse.from(posts);
	}

	public PageResponse<CommentResponse> getPublicComments(Long userId, int page, int size) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}

		Pageable pageable = createPageable(page, size);

		Page<CommentResponse> comments = commentRepository.findPublicCommentsByUserId(userId, pageable).map(CommentResponse::from);

		return PageResponse.from(comments);
	}

	public List<ChannelResponse> getMySubscribedChannels(Long userId) {
		return channelSubscriptionRepository.findMySubscriptions(userId).stream().map(subscription -> {
			long subscriberCount = channelSubscriptionRepository.countByChannel_ChannelId(subscription.getChannel().getChannelId());

			return ChannelResponse.from(subscription.getChannel(), subscriberCount, true);
		}).toList();
	}
}