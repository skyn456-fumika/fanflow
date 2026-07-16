package com.fanflow.domain.block;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserBlockRepository extends JpaRepository<UserBlock, Long> {

	boolean existsByBlocker_UserIdAndBlocked_UserId(Long blockerId, Long blockedId);

	Optional<UserBlock> findByBlocker_UserIdAndBlocked_UserId(Long blockerId, Long blockedId);

	@Query(value = """
			SELECT ub
			FROM UserBlock ub
			JOIN FETCH ub.blocked blocked
			WHERE ub.blocker.userId = :blockerId
			""", countQuery = """
			SELECT COUNT(ub)
			FROM UserBlock ub
			WHERE ub.blocker.userId = :blockerId
			""")
	Page<UserBlock> findMyBlocks(@Param("blockerId") Long blockerId, Pageable pageable);

	@Query("""
			SELECT ub.blocked.userId
			FROM UserBlock ub
			WHERE ub.blocker.userId = :blockerId
			""")
	List<Long> findBlockedUserIdsByBlockerId(@Param("blockerId") Long blockerId);
}