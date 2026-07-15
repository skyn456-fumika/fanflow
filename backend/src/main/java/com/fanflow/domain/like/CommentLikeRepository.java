package com.fanflow.domain.like;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

	boolean existsByComment_CommentIdAndUser_UserId(Long commentId, Long userId);

	Optional<CommentLike> findByComment_CommentIdAndUser_UserId(Long commentId, Long userId);

	List<CommentLike> findByComment_CommentIdInAndUser_UserId(Collection<Long> commentIds, Long userId);
}