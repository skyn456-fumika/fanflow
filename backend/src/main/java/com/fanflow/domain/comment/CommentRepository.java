package com.fanflow.domain.comment;

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
			WHERE p.postId = :postId
			  AND c.deleted = false
			  AND c.blind = false
			ORDER BY c.createdAt ASC
			""")
	List<Comment> findVisibleCommentsByPostId(@Param("postId") Long postId);

	@Query(value = """
			SELECT c
			FROM Comment c
			JOIN FETCH c.post p
			JOIN FETCH c.writer w
			WHERE (:keyword IS NULL
			    OR c.content LIKE CONCAT('%', :keyword, '%')
			    OR w.nickname LIKE CONCAT('%', :keyword, '%')
			    OR w.email LIKE CONCAT('%', :keyword, '%'))
			""", countQuery = """
			SELECT COUNT(c)
			FROM Comment c
			JOIN c.writer w
			WHERE (:keyword IS NULL
			    OR c.content LIKE CONCAT('%', :keyword, '%')
			    OR w.nickname LIKE CONCAT('%', :keyword, '%')
			    OR w.email LIKE CONCAT('%', :keyword, '%'))
			""")
	Page<Comment> searchAdminComments(@Param("keyword") String keyword, Pageable pageable);

	@Query(value = """
			SELECT c
			FROM Comment c
			JOIN FETCH c.post p
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
}