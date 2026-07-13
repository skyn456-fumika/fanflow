package com.fanflow.domain.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Page<Notification> findByReceiver_UserIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

	long countByReceiver_UserIdAndReadStatusFalse(Long receiverId);
}