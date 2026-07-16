package com.fanflow.domain.channelmember;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChannelMemberRepository extends JpaRepository<ChannelMember, Long> {

	boolean existsByChannel_ChannelIdAndRole(Long channelId, ChannelMemberRole role);

	boolean existsByChannel_ChannelIdAndUser_UserId(Long channelId, Long userId);

	Optional<ChannelMember> findByChannel_ChannelIdAndRole(Long channelId, ChannelMemberRole role);

	Optional<ChannelMember> findByChannel_ChannelIdAndUser_UserId(Long channelId, Long userId);

	@Query("""
			SELECT cm
			FROM ChannelMember cm
			JOIN FETCH cm.user u
			WHERE cm.channel.channelId = :channelId
			ORDER BY cm.createdAt ASC
			""")
	List<ChannelMember> findMembersByChannelId(@Param("channelId") Long channelId);

	@Query("""
			SELECT cm
			FROM ChannelMember cm
			JOIN FETCH cm.channel c
			WHERE cm.user.userId = :userId
			  AND cm.role = :role
			  AND c.active = true
			ORDER BY cm.createdAt ASC
			""")
	List<ChannelMember> findActiveChannelsByUserIdAndRole(@Param("userId") Long userId, @Param("role") ChannelMemberRole role);

	boolean existsByChannel_ChannelIdAndUser_UserIdAndRole(Long channelId, Long userId, ChannelMemberRole role);

	Optional<ChannelMember> findByChannel_ChannelIdAndUser_UserIdAndRole(Long channelId, Long userId, ChannelMemberRole role);
}
