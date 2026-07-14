package com.fanflow.domain.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReportRepository extends JpaRepository<Report, Long> {

	boolean existsByReporter_UserIdAndTargetTypeAndTargetId(Long reporterId, ReportTargetType targetType, Long targetId);

	@Query(value = """
			SELECT r
			FROM Report r
			JOIN FETCH r.reporter u
			WHERE (:status IS NULL OR r.status = :status)
			  AND (:targetType IS NULL OR r.targetType = :targetType)
			  AND (
			        :channelSlug IS NULL
			        OR (
			            r.targetType = com.fanflow.domain.report.ReportTargetType.POST
			            AND EXISTS (
			                SELECT 1
			                FROM Post p
			                JOIN p.board b
			                JOIN b.channel ch
			                WHERE p.postId = r.targetId
			                  AND ch.slug = :channelSlug
			            )
			        )
			        OR (
			            r.targetType = com.fanflow.domain.report.ReportTargetType.COMMENT
			            AND EXISTS (
			                SELECT 1
			                FROM Comment c
			                JOIN c.post p
			                JOIN p.board b
			                JOIN b.channel ch
			                WHERE c.commentId = r.targetId
			                  AND ch.slug = :channelSlug
			            )
			        )
			  )
			""", countQuery = """
			SELECT COUNT(r)
			FROM Report r
			WHERE (:status IS NULL OR r.status = :status)
			  AND (:targetType IS NULL OR r.targetType = :targetType)
			  AND (
			        :channelSlug IS NULL
			        OR (
			            r.targetType = com.fanflow.domain.report.ReportTargetType.POST
			            AND EXISTS (
			                SELECT 1
			                FROM Post p
			                JOIN p.board b
			                JOIN b.channel ch
			                WHERE p.postId = r.targetId
			                  AND ch.slug = :channelSlug
			            )
			        )
			        OR (
			            r.targetType = com.fanflow.domain.report.ReportTargetType.COMMENT
			            AND EXISTS (
			                SELECT 1
			                FROM Comment c
			                JOIN c.post p
			                JOIN p.board b
			                JOIN b.channel ch
			                WHERE c.commentId = r.targetId
			                  AND ch.slug = :channelSlug
			            )
			        )
			  )
			""")
	Page<Report> searchReports(@Param("channelSlug") String channelSlug, @Param("status") ReportStatus status,
			@Param("targetType") ReportTargetType targetType, Pageable pageable);

	long countByStatus(ReportStatus status);
}