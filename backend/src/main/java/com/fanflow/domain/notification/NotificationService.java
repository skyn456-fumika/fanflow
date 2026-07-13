package com.fanflow.domain.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.notification.dto.NotificationResponse;
import com.fanflow.domain.notification.dto.NotificationUnreadCountResponse;
import com.fanflow.domain.user.User;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

	private final NotificationRepository notificationRepository;

	@Transactional
	public void createCommentOnPostNotification(User receiver, Long postId, Long commentId, String commenterNickname) {
		if (receiver == null || !receiver.isActive()) {
			return;
		}

		String message = commenterNickname + "님이 내 게시글에 댓글을 남겼습니다.";

		Notification notification = Notification.builder().receiver(receiver).type(NotificationType.COMMENT_ON_POST).message(message)
				.targetPostId(postId).targetCommentId(commentId).build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void createPostBlindedNotification(User receiver, Long postId) {
		if (receiver == null || !receiver.isActive()) {
			return;
		}

		Notification notification = Notification.builder().receiver(receiver).type(NotificationType.POST_BLINDED)
				.message("작성한 게시글이 관리자에 의해 블라인드 처리되었습니다.").targetPostId(postId).build();

		notificationRepository.save(notification);
	}

	@Transactional
	public void createCommentBlindedNotification(User receiver, Long postId, Long commentId) {
		if (receiver == null || !receiver.isActive()) {
			return;
		}

		Notification notification = Notification.builder().receiver(receiver).type(NotificationType.COMMENT_BLINDED)
				.message("작성한 댓글이 관리자에 의해 블라인드 처리되었습니다.").targetPostId(postId).targetCommentId(commentId).build();

		notificationRepository.save(notification);
	}

	public PageResponse<NotificationResponse> getMyNotifications(Long userId, Pageable pageable) {
		Page<NotificationResponse> notifications = notificationRepository.findByReceiver_UserIdOrderByCreatedAtDesc(userId, pageable)
				.map(NotificationResponse::from);

		return PageResponse.from(notifications);
	}

	public NotificationUnreadCountResponse getUnreadCount(Long userId) {
		long unreadCount = notificationRepository.countByReceiver_UserIdAndReadStatusFalse(userId);

		return NotificationUnreadCountResponse.builder().unreadCount(unreadCount).build();
	}

	@Transactional
	public void readNotification(Long notificationId, Long userId) {
		Notification notification = notificationRepository.findById(notificationId)
				.orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE));

		if (!notification.getReceiver().getUserId().equals(userId)) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}

		notification.read();
	}
}