package com.fanflow.domain.board;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BoardDataInitializer implements ApplicationRunner {

	private final BoardRepository boardRepository;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		createBoardIfNotExists("NOTICE", "공지사항", "관리자 공지 게시판", 1);

		createBoardIfNotExists("FREE", "자유게시판", "팬들의 자유로운 소통 공간", 2);

		createBoardIfNotExists("FAN_ART", "팬아트", "팬아트 이미지 게시판", 3);

		createBoardIfNotExists("REVIEW", "방송후기", "방송 시청 후기 게시판", 4);
	}

	private void createBoardIfNotExists(String code, String name, String description, int sortOrder) {
		if (boardRepository.existsByCode(code)) {
			return;
		}

		Board board = Board.builder().code(code).name(name).description(description).sortOrder(sortOrder).active(true).build();

		boardRepository.save(board);
	}
}