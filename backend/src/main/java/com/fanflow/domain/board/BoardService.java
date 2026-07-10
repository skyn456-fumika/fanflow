package com.fanflow.domain.board;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.board.dto.BoardResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

	private final BoardRepository boardRepository;

	public List<BoardResponse> getBoards() {
		return boardRepository.findByActiveTrueOrderBySortOrderAsc().stream().map(BoardResponse::from).toList();
	}
}