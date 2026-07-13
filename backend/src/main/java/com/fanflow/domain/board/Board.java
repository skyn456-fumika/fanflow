package com.fanflow.domain.board;

import com.fanflow.domain.channel.Channel;
import com.fanflow.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "boards")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Board extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long boardId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	@Column(nullable = false, length = 30)
	private String code;

	@Column(nullable = false, length = 50)
	private String name;

	@Column(length = 255)
	private String description;

	@Column(nullable = false)
	private int sortOrder;

	@Column(nullable = false)
	private boolean active;

	@Builder
	public Board(Channel channel, String code, String name, String description, int sortOrder, boolean active) {
		this.channel = channel;
		this.code = code;
		this.name = name;
		this.description = description;
		this.sortOrder = sortOrder;
		this.active = active;
	}

	public void update(String code, String name, String description, int sortOrder) {
		this.code = code;
		this.name = name;
		this.description = description;
		this.sortOrder = sortOrder;
	}

	public void activate() {
		this.active = true;
	}

	public void deactivate() {
		this.active = false;
	}
}