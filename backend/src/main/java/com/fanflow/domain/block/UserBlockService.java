package com.fanflow.domain.block;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.block.dto.BlockedUserResponse;
import com.fanflow.domain.block.dto.UserBlockStatusResponse;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserBlockService {

	private final UserBlockRepository userBlockRepository;
	private final UserRepository userRepository;

	@Transactional
	public UserBlockStatusResponse blockUser(Long blockerId, Long blockedId) {
		validateNotSelf(blockerId, blockedId);

		User blocker = getActiveUser(blockerId);
		User blocked = getActiveUser(blockedId);

		boolean alreadyBlocked = userBlockRepository.existsByBlocker_UserIdAndBlocked_UserId(blockerId, blockedId);

		if (alreadyBlocked) {
			throw new BusinessException(ErrorCode.USER_ALREADY_BLOCKED);
		}

		UserBlock userBlock = UserBlock.builder().blocker(blocker).blocked(blocked).build();

		userBlockRepository.save(userBlock);

		return UserBlockStatusResponse.of(blockedId, true);
	}

	@Transactional
	public UserBlockStatusResponse unblockUser(Long blockerId, Long blockedId) {
		validateNotSelf(blockerId, blockedId);

		UserBlock userBlock = userBlockRepository.findByBlocker_UserIdAndBlocked_UserId(blockerId, blockedId)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_BLOCK_NOT_FOUND));

		userBlockRepository.delete(userBlock);

		return UserBlockStatusResponse.of(blockedId, false);
	}

	public UserBlockStatusResponse getBlockStatus(Long blockerId, Long targetUserId) {
		if (blockerId.equals(targetUserId)) {
			return UserBlockStatusResponse.of(targetUserId, false);
		}

		boolean blocked = userBlockRepository.existsByBlocker_UserIdAndBlocked_UserId(blockerId, targetUserId);

		return UserBlockStatusResponse.of(targetUserId, blocked);
	}

	public PageResponse<BlockedUserResponse> getMyBlocks(Long blockerId, int page, int size) {
		Pageable pageable = createPageable(page, size);

		Page<BlockedUserResponse> blocks = userBlockRepository.findMyBlocks(blockerId, pageable).map(BlockedUserResponse::from);

		return PageResponse.from(blocks);
	}

	public boolean isBlocked(Long blockerId, Long blockedId) {
		if (blockerId == null || blockedId == null || blockerId.equals(blockedId)) {
			return false;
		}

		return userBlockRepository.existsByBlocker_UserIdAndBlocked_UserId(blockerId, blockedId);
	}

	private void validateNotSelf(Long blockerId, Long blockedId) {
		if (blockerId.equals(blockedId)) {
			throw new BusinessException(ErrorCode.CANNOT_BLOCK_SELF);
		}
	}

	private User getActiveUser(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}

		return user;
	}

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
}