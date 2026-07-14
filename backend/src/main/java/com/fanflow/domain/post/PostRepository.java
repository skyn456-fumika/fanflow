package com.fanflow.domain.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

	@Query(value = """
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH p.writer w
			WHERE p.deleted = false
			  AND p.blind = false
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND (
			        :keyword IS NULL
			        OR p.title LIKE CONCAT('%', :keyword, '%')
			        OR p.content LIKE CONCAT('%', :keyword, '%')
			      )
			""", countQuery = """
			SELECT COUNT(p)
			FROM Post p
			JOIN p.board b
			WHERE p.deleted = false
			  AND p.blind = false
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND (
			        :keyword IS NULL
			        OR p.title LIKE CONCAT('%', :keyword, '%')
			        OR p.content LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	Page<Post> searchPosts(@Param("boardCode") String boardCode, @Param("keyword") String keyword, Pageable pageable);

	@Query(value = """
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE (:channelSlug IS NULL OR c.slug = :channelSlug)
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND (
			        :keyword IS NULL
			        OR p.title LIKE CONCAT('%', :keyword, '%')
			        OR p.content LIKE CONCAT('%', :keyword, '%')
			      )
			""", countQuery = """
			SELECT COUNT(p)
			FROM Post p
			JOIN p.board b
			JOIN b.channel c
			WHERE (:channelSlug IS NULL OR c.slug = :channelSlug)
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND (
			        :keyword IS NULL
			        OR p.title LIKE CONCAT('%', :keyword, '%')
			        OR p.content LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	Page<Post> searchAdminPosts(@Param("channelSlug") String channelSlug, @Param("boardCode") String boardCode, @Param("keyword") String keyword,
			Pageable pageable);

	// main
	@Query(value = """
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE w.userId = :userId
			  AND p.deleted = false
			""", countQuery = """
			SELECT COUNT(p)
			FROM Post p
			JOIN p.writer w
			WHERE w.userId = :userId
			  AND p.deleted = false
			""")
	Page<Post> findMyPosts(@Param("userId") Long userId, Pageable pageable);

	@Query("""
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE p.deleted = false
			  AND p.blind = false
			  AND p.notice = true
			ORDER BY p.createdAt DESC
			""")
	List<Post> findMainNoticePosts(Pageable pageable);

	@Query("""
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE p.deleted = false
			  AND p.blind = false
			  AND (
			       p.likeCount > 0
			       OR p.viewCount >= 10
			  )
			ORDER BY p.likeCount DESC, p.viewCount DESC, p.createdAt DESC
			""")
	List<Post> findMainPopularPosts(Pageable pageable);

	@Query("""
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE p.deleted = false
			  AND p.blind = false
			ORDER BY p.createdAt DESC
			""")
	List<Post> findMainRecentPosts(Pageable pageable);

	@Query("""
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE p.deleted = false
			  AND p.blind = false
			  AND p.commentCount > 0
			ORDER BY p.commentCount DESC, p.createdAt DESC
			""")
	List<Post> findMainCommentedPosts(Pageable pageable);

	long countByDeletedFalse();

	long countByDeletedFalseAndCreatedAtBetween(LocalDateTime start, LocalDateTime end);

	long countByDeletedFalseAndBlindTrue();

	@Query(value = """
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE w.userId = :userId
			  AND p.deleted = false
			  AND p.blind = false
			""", countQuery = """
			SELECT COUNT(p)
			FROM Post p
			JOIN p.writer w
			WHERE w.userId = :userId
			  AND p.deleted = false
			  AND p.blind = false
			""")
	Page<Post> findPublicPostsByUserId(@Param("userId") Long userId, Pageable pageable);

	long countByWriter_UserIdAndDeletedFalseAndBlindFalse(Long userId);

	@Query(value = """
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE c.slug = :channelSlug
			  AND c.active = true
			  AND b.active = true
			  AND p.deleted = false
			  AND p.blind = false
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND (
			        :keyword IS NULL
			        OR p.title LIKE CONCAT('%', :keyword, '%')
			        OR p.content LIKE CONCAT('%', :keyword, '%')
			      )
			""", countQuery = """
			SELECT COUNT(p)
			FROM Post p
			JOIN p.board b
			JOIN b.channel c
			WHERE c.slug = :channelSlug
			  AND c.active = true
			  AND b.active = true
			  AND p.deleted = false
			  AND p.blind = false
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND (
			        :keyword IS NULL
			        OR p.title LIKE CONCAT('%', :keyword, '%')
			        OR p.content LIKE CONCAT('%', :keyword, '%')
			      )
			""")
	Page<Post> searchPostsByChannel(@Param("channelSlug") String channelSlug, @Param("boardCode") String boardCode, @Param("keyword") String keyword,
			Pageable pageable);

	@Query("""
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE p.postId = :postId
			""")
	Optional<Post> findDetailById(@Param("postId") Long postId);

	@Query("""
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE c.slug = :channelSlug
			  AND c.active = true
			  AND b.active = true
			  AND p.deleted = false
			  AND p.blind = false
			  AND p.notice = true
			ORDER BY p.createdAt DESC
			""")
	List<Post> findChannelHomeNoticePosts(@Param("channelSlug") String channelSlug, Pageable pageable);

	@Query("""
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE c.slug = :channelSlug
			  AND c.active = true
			  AND b.active = true
			  AND p.deleted = false
			  AND p.blind = false
			  AND (
			       p.likeCount > 0
			       OR p.viewCount >= 10
			  )
			ORDER BY p.likeCount DESC, p.viewCount DESC, p.createdAt DESC
			""")
	List<Post> findChannelHomePopularPosts(@Param("channelSlug") String channelSlug, Pageable pageable);

	@Query("""
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE c.slug = :channelSlug
			  AND c.active = true
			  AND b.active = true
			  AND p.deleted = false
			  AND p.blind = false
			ORDER BY p.createdAt DESC
			""")
	List<Post> findChannelHomeRecentPosts(@Param("channelSlug") String channelSlug, Pageable pageable);

	@Query(value = """
			SELECT p
			FROM Post p
			JOIN FETCH p.board b
			JOIN FETCH b.channel c
			JOIN FETCH p.writer w
			WHERE p.deleted = false
			  AND p.blind = false
			  AND c.active = true
			  AND b.active = true
			  AND (:channelSlug IS NULL OR c.slug = :channelSlug)
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND EXISTS (
			      SELECT 1
			      FROM ChannelSubscription cs
			      WHERE cs.channel = c
			        AND cs.user.userId = :userId
			  )
			""", countQuery = """
			SELECT COUNT(p)
			FROM Post p
			JOIN p.board b
			JOIN b.channel c
			WHERE p.deleted = false
			  AND p.blind = false
			  AND c.active = true
			  AND b.active = true
			  AND (:channelSlug IS NULL OR c.slug = :channelSlug)
			  AND (:boardCode IS NULL OR b.code = :boardCode)
			  AND EXISTS (
			      SELECT 1
			      FROM ChannelSubscription cs
			      WHERE cs.channel = c
			        AND cs.user.userId = :userId
			  )
			""")
	Page<Post> findSubscriptionFeedPosts(@Param("userId") Long userId, @Param("channelSlug") String channelSlug, @Param("boardCode") String boardCode,
			Pageable pageable);
}