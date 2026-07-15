package com.fanflow.domain.bookmark;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostBookmarkRepository extends JpaRepository<PostBookmark, Long> {

	boolean existsByPost_PostIdAndUser_UserId(Long postId, Long userId);

	Optional<PostBookmark> findByPost_PostIdAndUser_UserId(Long postId, Long userId);

	@Query(value = """
			SELECT pb
			FROM PostBookmark pb
			JOIN FETCH pb.post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE pb.user.userId = :userId
			  AND p.deleted = false
			  AND p.blind = false
			  AND b.active = true
			  AND c.active = true
			""", countQuery = """
			SELECT COUNT(pb)
			FROM PostBookmark pb
			JOIN pb.post p
			JOIN p.board b
			JOIN b.channel c
			WHERE pb.user.userId = :userId
			  AND p.deleted = false
			  AND p.blind = false
			  AND b.active = true
			  AND c.active = true
			""")
	Page<PostBookmark> findMyPostBookmarks(@Param("userId") Long userId, Pageable pageable);
}