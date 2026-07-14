package com.fanflow.domain.channel;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
}