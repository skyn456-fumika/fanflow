package com.fanflow.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.post.Post;
import com.fanflow.domain.post.PostRepository;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminPostService {

	private final PostRepository postRepository;

	public PageResponse<PostListResponse> getPosts(String boardCode, String keyword, int page, int size) {
		page = Math.max(page, 0);

		if (size < 1) {
			size = 10;
		}

		if (size > 50) {
			size = 50;
		}

		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));

		Page<PostListResponse> posts = postRepository.searchAdminPosts(normalize(boardCode), normalize(keyword), pageable)
				.map(PostListResponse::from);

		return PageResponse.from(posts);
	}

	@Transactional
	public void blindPost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		post.blind();
	}

	@Transactional
	public void unblindPost(Long postId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new BusinessException(ErrorCode.POST_NOT_FOUND));

		if (post.isDeleted()) {
			throw new BusinessException(ErrorCode.POST_NOT_FOUND);
		}

		post.unblind();
	}

	private String normalize(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim();
	}
}