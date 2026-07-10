package com.fanflow.domain.post;

import com.fanflow.domain.board.Board;
import com.fanflow.domain.user.User;
import com.fanflow.global.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long postId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "board_id", nullable = false)
	private Board board;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User writer;

	@Column(nullable = false, length = 200)
	private String title;

	@Lob
	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String content;

	@Column(nullable = false)
	private int viewCount;

	@Column(nullable = false)
	private int likeCount;

	@Column(nullable = false)
	private int commentCount;

	@Column(nullable = false)
	private boolean notice;

	@Column(nullable = false)
	private boolean blind;

	@Column(nullable = false)
	private boolean deleted;

	@Builder
	public Post(Board board, User writer, String title, String content, boolean notice) {
		this.board = board;
		this.writer = writer;
		this.title = title;
		this.content = content;
		this.viewCount = 0;
		this.likeCount = 0;
		this.commentCount = 0;
		this.notice = notice;
		this.blind = false;
		this.deleted = false;
	}

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public void increaseViewCount() {
		this.viewCount++;
	}

	public void increaseLikeCount() {
		this.likeCount++;
	}

	public void decreaseLikeCount() {
		if (this.likeCount > 0) {
			this.likeCount--;
		}
	}

	public void increaseCommentCount() {
		this.commentCount++;
	}

	public void decreaseCommentCount() {
		if (this.commentCount > 0) {
			this.commentCount--;
		}
	}

	public void blind() {
		this.blind = true;
	}

	public void unblind() {
		this.blind = false;
	}

	public void delete() {
		this.deleted = true;
	}

	public boolean isWriter(Long userId) {
		return this.writer.getUserId().equals(userId);
	}
}