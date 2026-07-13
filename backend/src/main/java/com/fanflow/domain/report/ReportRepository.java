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
			""", countQuery = """
			SELECT COUNT(r)
			FROM Report r
			WHERE (:status IS NULL OR r.status = :status)
			  AND (:targetType IS NULL OR r.targetType = :targetType)
			""")
	Page<Report> searchReports(@Param("status") ReportStatus status, @Param("targetType") ReportTargetType targetType, Pageable pageable);

	long countByStatus(ReportStatus status);
}