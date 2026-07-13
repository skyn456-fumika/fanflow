package com.fanflow.domain.board;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.board.dto.BoardCreateRequest;
import com.fanflow.domain.board.dto.BoardResponse;
import com.fanflow.domain.board.dto.BoardUpdateRequest;
import com.fanflow.domain.channel.Channel;
import com.fanflow.domain.channel.ChannelRepository;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

	private final BoardRepository boardRepository;
	private final ChannelRepository channelRepository;

	public List<BoardResponse> getBoards() {
		return boardRepository.findActiveBoardsWithChannel().stream().map(BoardResponse::from).toList();
	}

	public List<BoardResponse> getBoardsByChannel(String channelSlug) {
		return boardRepository.findActiveBoardsByChannelSlug(channelSlug).stream().map(BoardResponse::from).toList();
	}

	@Transactional
	public void createDefaultBoards(Channel channel) {
		createDefaultBoardIfNotExists(channel, "NOTICE", "공지사항", "중요한 공지와 안내를 확인하는 게시판입니다.", 1);
		createDefaultBoardIfNotExists(channel, "FREE", "자유게시판", "팬들이 자유롭게 이야기를 나누는 게시판입니다.", 2);
		createDefaultBoardIfNotExists(channel, "FAN_ART", "팬아트", "팬아트와 창작물을 공유하는 게시판입니다.", 3);
		createDefaultBoardIfNotExists(channel, "REVIEW", "후기", "방송, 이벤트, 굿즈 등에 대한 후기를 남기는 게시판입니다.", 4);
	}

	private void createDefaultBoardIfNotExists(Channel channel, String code, String name, String description, int sortOrder) {

		if (boardRepository.existsByChannel_ChannelIdAndCode(channel.getChannelId(), code)) {
			return;
		}

		Board board = Board.builder().channel(channel).code(code).name(name).description(description).sortOrder(sortOrder).active(true).build();

		boardRepository.save(board);
	}

	public List<BoardResponse> getAdminBoardsByChannel(Long channelId) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		return boardRepository.findByChannel_ChannelIdOrderBySortOrderAsc(channel.getChannelId()).stream().map(BoardResponse::from).toList();
	}

	@Transactional
	public BoardResponse createBoard(Long channelId, BoardCreateRequest request) {
		Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new BusinessException(ErrorCode.CHANNEL_NOT_FOUND));

		String code = normalizeCode(request.getCode());

		if (boardRepository.existsByChannel_ChannelIdAndCode(channel.getChannelId(), code)) {
			throw new BusinessException(ErrorCode.BOARD_CODE_DUPLICATED);
		}

		Board board = Board.builder().channel(channel).code(code).name(request.getName().trim())
				.description(normalizeNullable(request.getDescription())).sortOrder(request.getSortOrder()).active(true).build();

		Board savedBoard = boardRepository.save(board);

		return BoardResponse.from(savedBoard);
	}

	@Transactional
	public BoardResponse updateBoard(Long channelId, Long boardId, BoardUpdateRequest request) {
		Board board = boardRepository.findByChannel_ChannelIdAndBoardId(channelId, boardId)
				.orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

		String code = normalizeCode(request.getCode());

		if (boardRepository.existsByChannel_ChannelIdAndCodeAndBoardIdNot(channelId, code, boardId)) {
			throw new BusinessException(ErrorCode.BOARD_CODE_DUPLICATED);
		}

		board.update(code, request.getName().trim(), normalizeNullable(request.getDescription()), request.getSortOrder());

		return BoardResponse.from(board);
	}

	@Transactional
	public void activateBoard(Long channelId, Long boardId) {
		Board board = boardRepository.findByChannel_ChannelIdAndBoardId(channelId, boardId)
				.orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

		board.activate();
	}

	@Transactional
	public void deactivateBoard(Long channelId, Long boardId) {
		Board board = boardRepository.findByChannel_ChannelIdAndBoardId(channelId, boardId)
				.orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

		board.deactivate();
	}

	private String normalizeCode(String code) {
		if (code == null) {
			return null;
		}

		return code.trim().toUpperCase();
	}

	private String normalizeNullable(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}
}