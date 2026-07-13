package com.fanflow.domain.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<Board, Long> {

	Optional<Board> findByCode(String code);

	boolean existsByCode(String code);

	List<Board> findByActiveTrueOrderBySortOrderAsc();

	@Query("""
			SELECT b
			FROM Board b
			JOIN FETCH b.channel c
			WHERE b.active = true
			ORDER BY b.sortOrder ASC
			""")
	List<Board> findActiveBoardsWithChannel();

	@Query("""
			SELECT b
			FROM Board b
			JOIN FETCH b.channel c
			WHERE c.slug = :channelSlug
			  AND b.active = true
			ORDER BY b.sortOrder ASC
			""")
	List<Board> findActiveBoardsByChannelSlug(@Param("channelSlug") String channelSlug);

	@Query("""
			SELECT b
			FROM Board b
			JOIN FETCH b.channel c
			WHERE c.slug = :channelSlug
			  AND b.code = :code
			""")
	Optional<Board> findByChannelSlugAndCode(@Param("channelSlug") String channelSlug, @Param("code") String code);
}