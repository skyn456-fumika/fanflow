package com.fanflow.domain.board;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.board.dto.BoardResponse;
import com.fanflow.global.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;

	@GetMapping("/api/boards")
	public ApiResponse<List<BoardResponse>> getBoards() {
		List<BoardResponse> response = boardService.getBoards();
		return ApiResponse.success("게시판 목록 조회에 성공했습니다.", response);
	}

	@GetMapping("/api/channels/{channelSlug}/boards")
	public ApiResponse<List<BoardResponse>> getBoardsByChannel(@PathVariable String channelSlug) {
		List<BoardResponse> response = boardService.getBoardsByChannel(channelSlug);

		return ApiResponse.success("채널 게시판 목록 조회에 성공했습니다.", response);
	}
}