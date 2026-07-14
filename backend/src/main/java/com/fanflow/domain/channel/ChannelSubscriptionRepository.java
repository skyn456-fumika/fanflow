package com.fanflow.domain.channel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserStatus;

public interface ChannelSubscriptionRepository extends JpaRepository<ChannelSubscription, Long> {

	boolean existsByUser_UserIdAndChannel_ChannelId(Long userId, Long channelId);

	Optional<ChannelSubscription> findByUser_UserIdAndChannel_ChannelId(Long userId, Long channelId);

	long countByChannel_ChannelId(Long channelId);

	long countByUser_UserId(Long userId);

	@Query("""
			SELECT cs
			FROM ChannelSubscription cs
			JOIN FETCH cs.channel c
			WHERE cs.user.userId = :userId
			  AND c.active = true
			ORDER BY cs.createdAt DESC
			""")
	List<ChannelSubscription> findMySubscriptions(@Param("userId") Long userId);

	@Query("""
			SELECT cs.user
			FROM ChannelSubscription cs
			WHERE cs.channel.channelId = :channelId
			  AND cs.user.userId <> :writerId
			  AND cs.user.status = :status
			""")
	List<User> findNotificationReceivers(@Param("channelId") Long channelId, @Param("writerId") Long writerId, @Param("status") UserStatus status);
}