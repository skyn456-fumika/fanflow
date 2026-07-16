package com.fanflow.domain.comment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {

	@Query("""
			SELECT c
			FROM Comment c
			JOIN FETCH c.post p
			JOIN FETCH c.writer w
			LEFT JOIN FETCH c.parent parent
			LEFT JOIN FETCH parent.writer parentWriter
			WHERE p.postId = :postId

			  AND (
			        :includeBlind = true
			        OR c.blind = false
			      )

			  AND (
			        :includeBlind = true
			        OR :viewerId IS NULL
			        OR NOT EXISTS (
			             SELECT writerBlock.userBlockId
			             FROM UserBlock writerBlock
			             WHERE writerBlock.blocker.userId = :viewerId
			               AND writerBlock.blocked = w
			        )
			      )

			  AND (
			        c.parent IS NULL
			        OR :includeBlind = true
			        OR :viewerId IS NULL
			        OR NOT EXISTS (
			             SELECT parentBlock.userBlockId
			             FROM UserBlock parentBlock
			             WHERE parentBlock.blocker.userId = :viewerId
			               AND parentBlock.blocked = parentWriter
			        )
			      )

			  AND (
			        c.parent IS NULL
			        OR :includeBlind = true
			        OR parent.blind = false
			      )

			  AND (
			        c.deleted = false
			        OR (
			             c.parent IS NULL
			             AND EXISTS (
			                  SELECT reply.commentId
			                  FROM Comment reply
			                  WHERE reply.parent = c
			                    AND reply.deleted = false
			                    AND (
			                          :includeBlind = true
			                          OR reply.blind = false
			                        )
			             )
			        )
			      )

			ORDER BY
			  CASE
			    WHEN c.parent IS NULL
			      THEN c.commentId
			    ELSE c.parent.commentId
			  END ASC,
			  CASE
			    WHEN c.parent IS NULL
			      THEN 0
			    ELSE 1
			  END ASC,
			  c.createdAt ASC
			""")
	List<Comment> findVisibleCommentsByPostId(@Param("postId") Long postId, @Param("viewerId") Long viewerId,
			@Param("includeBlind") boolean includeBlind);

	@Query(value = """
			SELECT c
			FROM Comment c
			JOIN FETCH c.post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel ch
			JOIN FETCH c.writer w
			WHERE (:channelSlug IS NULL OR ch.slug = :channelSlug)
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND (
			        :keyword IS NULL
			        OR c.content LIKE CONCAT('%', :keyword, '%')
			        OR w.nickname LIKE CONCAT('%', :keyword, '%')
			        OR w.email LIKE CONCAT('%', :keyword, '%')
			        OR p.title LIKE CONCAT('%', :keyword, '%')
			      )
			""", countQuery = """
			SELECT COUNT(c)
			FROM Comment c
			JOIN c.post p
			JOIN p.board b
			JOIN b.channel ch
			JOIN c.writer w
			WHERE (:channelSlug IS NULL OR ch.slug = :channelSlug)
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND (
			        :keyword IS NULL
			        OR c.content LIKE CONCAT('%', :keyword, '%')
			        OR w.nickname LIKE CONCAT('%', :keyword, '%')
			        OR w.email LIKE CONCAT('%', :keyword, '%')
			        OR p.title LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	Page<Comment> searchAdminComments(@Param("channelSlug") String channelSlug, @Param("boardCode") String boardCode,
			@Param("keyword") String keyword, Pageable pageable);

	@Query(value = """
			SELECT c
			FROM Comment c
			JOIN FETCH c.post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel ch
			JOIN FETCH c.writer w
			WHERE w.userId = :userId
			  AND c.deleted = false
			""", countQuery = """
			SELECT COUNT(c)
			FROM Comment c
			JOIN c.writer w
			WHERE w.userId = :userId
			  AND c.deleted = false
			""")
	Page<Comment> findMyComments(@Param("userId") Long userId, Pageable pageable);

	long countByDeletedFalse();

	long countByDeletedFalseAndCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	long countByDeletedFalseAndBlindTrue();

	@Query(value = """
			SELECT c
			FROM Comment c
			JOIN FETCH c.post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel ch
			JOIN FETCH c.writer w
			WHERE w.userId = :userId
			  AND c.deleted = false
			  AND c.blind = false
			  AND p.deleted = false
			  AND p.blind = false
			""", countQuery = """
			SELECT COUNT(c)
			FROM Comment c
			JOIN c.post p
			JOIN c.writer w
			WHERE w.userId = :userId
			  AND c.deleted = false
			  AND c.blind = false
			  AND p.deleted = false
			  AND p.blind = false
			""")
	Page<Comment> findPublicCommentsByUserId(@Param("userId") Long userId, Pageable pageable);

	@Query("""
			SELECT COUNT(c)
			FROM Comment c
			JOIN c.post p
			JOIN c.writer w
			WHERE w.userId = :userId
			  AND c.deleted = false
			  AND c.blind = false
			  AND p.deleted = false
			  AND p.blind = false
			""")
	long countPublicCommentsByUserId(@Param("userId") Long userId);
}