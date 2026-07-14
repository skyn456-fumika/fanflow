package com.fanflow.domain.feed;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.post.PostRepository;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.global.response.PageResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FeedService {

	private final PostRepository postRepository;

	public PageResponse<PostListResponse> getSubscriptionFeed(Long userId, int page, int size) {
		Pageable pageable = createPageable(page, size);

		Page<PostListResponse> posts = postRepository.findSubscriptionFeedPosts(userId, pageable).map(PostListResponse::from);

		return PageResponse.from(posts);
	}

	private Pageable createPageable(int page, int size) {
		if (page < 0) {
			page = 0;
		}

		if (size < 1) {
			size = 10;
		}

		if (size > 50) {
			size = 50;
		}

		return PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
	}
}