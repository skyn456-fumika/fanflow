package com.fanflow.domain.notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

	Page<Notification> findByReceiver_UserIdOrderByCreatedAtDesc(Long receiverId, Pageable pageable);

	long countByReceiver_UserIdAndReadStatusFalse(Long receiverId);

	@Modifying(clearAutomatically = true)
	@Query("""
			UPDATE Notification n
			SET n.readStatus = true
			WHERE n.receiver.userId = :receiverId
			  AND n.readStatus = false
			""")
	int readAllByReceiverId(@Param("receiverId") Long receiverId);

	@Modifying(clearAutomatically = true)
	@Query("""
			DELETE FROM Notification n
			WHERE n.receiver.userId = :receiverId
			  AND n.readStatus = true
			""")
	int deleteReadByReceiverId(@Param("receiverId") Long receiverId);
}