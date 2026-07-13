package com.fanflow.domain.board;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.board.dto.BoardResponse;
import com.fanflow.domain.channel.Channel;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

	private final BoardRepository boardRepository;

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
}