package com.fanflow.domain.board;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardRepository extends JpaRepository<Board, Long> {

	Optional<Board> findByCode(String code);

	boolean existsByCode(String code);

	List<Board> findByActiveTrueOrderBySortOrderAsc();
}