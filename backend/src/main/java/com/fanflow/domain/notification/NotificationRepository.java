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

	@Query(value = """
			SELECT n
			FROM Notification n
			WHERE n.receiver.userId = :receiverId
			  AND (
			        n.actorUserId IS NULL
			        OR NOT EXISTS (
			             SELECT ub.userBlockId
			             FROM UserBlock ub
			             WHERE ub.blocker.userId = :receiverId
			               AND ub.blocked.userId = n.actorUserId
			        )
			      )
			ORDER BY n.createdAt DESC
			""", countQuery = """
			SELECT COUNT(n)
			FROM Notification n
			WHERE n.receiver.userId = :receiverId
			  AND (
			        n.actorUserId IS NULL
			        OR NOT EXISTS (
			             SELECT ub.userBlockId
			             FROM UserBlock ub
			             WHERE ub.blocker.userId = :receiverId
			               AND ub.blocked.userId = n.actorUserId
			        )
			      )
			""")
	Page<Notification> findVisibleNotifications(@Param("receiverId") Long receiverId, Pageable pageable);

	@Query("""
			SELECT COUNT(n)
			FROM Notification n
			WHERE n.receiver.userId = :receiverId
			  AND n.readStatus = false
			  AND (
			        n.actorUserId IS NULL
			        OR NOT EXISTS (
			             SELECT ub.userBlockId
			             FROM UserBlock ub
			             WHERE ub.blocker.userId = :receiverId
			               AND ub.blocked.userId = n.actorUserId
			        )
			      )
			""")
	long countVisibleUnreadNotifications(@Param("receiverId") Long receiverId);

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

	boolean existsByReceiver_UserIdAndTypeAndTargetCommentIdAndActorUserId(Long receiverId, NotificationType type, Long targetCommentId,
			Long actorUserId);

	@Modifying
	@Query("""
			DELETE FROM Notification n
			WHERE n.receiver.userId = :receiverId
			  AND n.type = :type
			  AND n.targetCommentId = :commentId
			  AND n.actorUserId = :actorUserId
			""")
	int deleteCommentLikeNotification(@Param("receiverId") Long receiverId, @Param("type") NotificationType type, @Param("commentId") Long commentId,
			@Param("actorUserId") Long actorUserId);

	boolean existsByReceiver_UserIdAndTypeAndTargetPostIdAndActorUserId(Long receiverId, NotificationType type, Long targetPostId, Long actorUserId);

	@Modifying
	@Query("""
			DELETE FROM Notification n
			WHERE n.receiver.userId = :receiverId
			  AND n.type = :type
			  AND n.targetPostId = :postId
			  AND n.actorUserId = :actorUserId
			""")
	int deletePostLikeNotification(@Param("receiverId") Long receiverId, @Param("type") NotificationType type, @Param("postId") Long postId,
			@Param("actorUserId") Long actorUserId);
}