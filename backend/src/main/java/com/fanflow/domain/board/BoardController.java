package com.fanflow.domain.board;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fanflow.domain.board.dto.BoardCreateRequest;
import com.fanflow.domain.board.dto.BoardResponse;
import com.fanflow.domain.board.dto.BoardUpdateRequest;
import com.fanflow.global.response.ApiResponse;

import jakarta.validation.Valid;
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

	@GetMapping("/api/admin/channels/{channelId}/boards")
	public ApiResponse<List<BoardResponse>> getAdminBoardsByChannel(@PathVariable Long channelId) {
		List<BoardResponse> response = boardService.getAdminBoardsByChannel(channelId);

		return ApiResponse.success("관리자 채널 게시판 목록 조회에 성공했습니다.", response);
	}

	@PostMapping("/api/admin/channels/{channelId}/boards")
	public ApiResponse<BoardResponse> createBoard(@PathVariable Long channelId, @Valid @RequestBody BoardCreateRequest request) {
		BoardResponse response = boardService.createBoard(channelId, request);

		return ApiResponse.success("게시판이 생성되었습니다.", response);
	}

	@PutMapping("/api/admin/channels/{channelId}/boards/{boardId}")
	public ApiResponse<BoardResponse> updateBoard(@PathVariable Long channelId, @PathVariable Long boardId,
			@Valid @RequestBody BoardUpdateRequest request) {
		BoardResponse response = boardService.updateBoard(channelId, boardId, request);

		return ApiResponse.success("게시판이 수정되었습니다.", response);
	}

	@PatchMapping("/api/admin/channels/{channelId}/boards/{boardId}/activate")
	public ApiResponse<Void> activateBoard(@PathVariable Long channelId, @PathVariable Long boardId) {
		boardService.activateBoard(channelId, boardId);

		return ApiResponse.success("게시판이 활성화되었습니다.");
	}

	@PatchMapping("/api/admin/channels/{channelId}/boards/{boardId}/deactivate")
	public ApiResponse<Void> deactivateBoard(@PathVariable Long channelId, @PathVariable Long boardId) {
		boardService.deactivateBoard(channelId, boardId);

		return ApiResponse.success("게시판이 비활성화되었습니다.");
	}
}