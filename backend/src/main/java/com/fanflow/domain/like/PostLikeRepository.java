package com.fanflow.domain.like;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

	boolean existsByPost_PostIdAndUser_UserId(Long postId, Long userId);

	Optional<PostLike> findByPost_PostIdAndUser_UserId(Long postId, Long userId);

	@Query(value = """
			SELECT pl
			FROM PostLike pl
			JOIN FETCH pl.post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE pl.user.userId = :userId
			  AND p.deleted = false
			  AND p.blind = false
			""", countQuery = """
			SELECT COUNT(pl)
			FROM PostLike pl
			JOIN pl.post p
			WHERE pl.user.userId = :userId
			  AND p.deleted = false
			  AND p.blind = false
			""")
	Page<PostLike> findMyPostLikes(@Param("userId") Long userId, Pageable pageable);
}