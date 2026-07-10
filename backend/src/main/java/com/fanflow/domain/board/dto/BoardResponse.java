package com.fanflow.domain.board.dto;

import com.fanflow.domain.board.Board;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardResponse {

	private Long boardId;
	private String code;
	private String name;
	private String description;
	private int sortOrder;
	private boolean active;

	public static BoardResponse from(Board board) {
		return BoardResponse.builder().boardId(board.getBoardId()).code(board.getCode()).name(board.getName()).description(board.getDescription())
				.sortOrder(board.getSortOrder()).active(board.isActive()).build();
	}
}