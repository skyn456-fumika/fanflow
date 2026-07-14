package com.fanflow.domain.notification;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.notification.dto.NotificationResponse;
import com.fanflow.domain.notification.dto.NotificationUnreadCountResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NotificationController {

	private final NotificationService notificationService;

	@GetMapping("/api/notifications")
	public ApiResponse<PageResponse<NotificationResponse>> getMyNotifications(@CurrentUser CustomUserDetails userDetails,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {

		if (page < 0) {
			page = 0;
		}

		if (size < 1) {
			size = 10;
		}

		if (size > 50) {
			size = 50;
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

		PageResponse<NotificationResponse> response = notificationService.getMyNotifications(userDetails.getUserId(), pageable);

		return ApiResponse.success("알림 목록 조회에 성공했습니다.", response);
	}

	@GetMapping("/api/notifications/unread-count")
	public ApiResponse<NotificationUnreadCountResponse> getUnreadCount(@CurrentUser CustomUserDetails userDetails) {
		NotificationUnreadCountResponse response = notificationService.getUnreadCount(userDetails.getUserId());

		return ApiResponse.success("읽지 않은 알림 수 조회에 성공했습니다.", response);
	}

	@PatchMapping("/api/notifications/{notificationId}/read")
	public ApiResponse<Void> readNotification(@PathVariable Long notificationId, @CurrentUser CustomUserDetails userDetails) {
		notificationService.readNotification(notificationId, userDetails.getUserId());

		return ApiResponse.success("알림을 읽음 처리했습니다.");
	}

	@PatchMapping("/api/notifications/read-all")
	public ApiResponse<Void> readAllNotifications(@CurrentUser CustomUserDetails userDetails) {
		notificationService.readAllNotifications(userDetails.getUserId());

		return ApiResponse.success("모든 알림을 읽음 처리했습니다.");
	}

	@DeleteMapping("/api/notifications/read")
	public ApiResponse<Void> deleteReadNotifications(@CurrentUser CustomUserDetails userDetails) {
		notificationService.deleteReadNotifications(userDetails.getUserId());

		return ApiResponse.success("읽은 알림을 모두 삭제했습니다.");
	}

	@DeleteMapping("/api/notifications/{notificationId}")
	public ApiResponse<Void> deleteNotification(@PathVariable Long notificationId, @CurrentUser CustomUserDetails userDetails) {
		notificationService.deleteNotification(notificationId, userDetails.getUserId());

		return ApiResponse.success("알림을 삭제했습니다.");
	}
}