package com.fanflow.domain.comment;

import com.fanflow.domain.post.Post;
import com.fanflow.domain.user.User;
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
@Table(name = "comments")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long commentId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "post_id", nullable = false)
	private Post post;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User writer;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Column(nullable = false)
	private boolean blind;

	@Column(nullable = false)
	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_comment_id")
	private Comment parent;

	@Column(nullable = false)
	private long likeCount;

	@Builder
	public Comment(Post post, User writer, String content, Comment parent) {
		this.post = post;
		this.writer = writer;
		this.content = content;
		this.parent = parent;
		this.blind = false;
		this.deleted = false;
		this.likeCount = 0L;
	}

	public void update(String content) {
		this.content = content;
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

	public boolean isReply() {
		return this.parent != null;
	}

	public boolean isRootComment() {
		return this.parent == null;
	}

	public void increaseLikeCount() {
		this.likeCount++;
	}

	public void decreaseLikeCount() {
		if (this.likeCount > 0) {
			this.likeCount--;
		}
	}
}