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

	public PageResponse<PostListResponse> getSubscriptionFeed(Long userId, String channelSlug, String sort, int page, int size) {
		String normalizedChannelSlug = normalize(channelSlug);

		Pageable pageable = createPageable(page, size, sort);

		Page<PostListResponse> posts = postRepository.findSubscriptionFeedPosts(userId, normalizedChannelSlug, pageable).map(PostListResponse::from);

		return PageResponse.from(posts);
	}

	private Pageable createPageable(int page, int size, String sort) {
		if (page < 0) {
			page = 0;
		}

		if (size < 1) {
			size = 10;
		}

		if (size > 50) {
			size = 50;
		}

		Sort feedSort;

		if ("popular".equalsIgnoreCase(sort)) {
			feedSort = Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("viewCount"), Sort.Order.desc("createdAt"));
		} else {
			feedSort = Sort.by(Sort.Order.desc("createdAt"));
		}

		return PageRequest.of(page, size, feedSort);
	}

	private String normalize(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}

		return value.trim().toLowerCase();
	}
}