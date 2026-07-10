package com.fanflow.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.admin.dto.AdminUserResponse;
import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;
import com.fanflow.domain.user.UserStatus;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminUserService {

	private final UserRepository userRepository;

	public PageResponse<AdminUserResponse> getUsers(String status, String keyword, int page, int size) {
		Pageable pageable = createPageable(page, size);

		Page<AdminUserResponse> users = userRepository.searchAdminUsers(parseStatus(status), normalize(keyword), pageable)
				.map(AdminUserResponse::from);

		return PageResponse.from(users);
	}

	@Transactional
	public void blockUser(Long targetUserId, Long adminUserId) {
		if (targetUserId.equals(adminUserId)) {
			throw new BusinessException(ErrorCode.CANNOT_BLOCK_SELF);
		}

		User user = userRepository.findById(targetUserId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (user.getStatus() == UserStatus.DELETED) {
			throw new BusinessException(ErrorCode.USER_NOT_FOUND);
		}

		user.block();
	}

	@Transactional
	public void activateUser(Long targetUserId) {
		User user = userRepository.findById(targetUserId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

		if (user.getStatus() == UserStatus.DELETED) {
			throw new BusinessException(ErrorCode.CANNOT_ACTIVATE_DELETED_USER);
		}

		user.activate();
	}

	private UserStatus parseStatus(String status) {
		if (status == null || status.isBlank()) {
			return null;
		}

		try {
			return UserStatus.valueOf(status.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
		}
	}

	private String normalize(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
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