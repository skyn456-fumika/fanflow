package com.fanflow.domain.main;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.post.PostRepository;
import com.fanflow.domain.post.dto.PostListResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainService {

	private final PostRepository postRepository;

	public MainResponse getMain() {
		List<PostListResponse> noticePosts = postRepository.findMainNoticePosts(PageRequest.of(0, 3)).stream().map(PostListResponse::from).toList();

		List<PostListResponse> popularPosts = postRepository.findMainPopularPosts(PageRequest.of(0, 5)).stream().map(PostListResponse::from).toList();

		List<PostListResponse> recentPosts = postRepository.findMainRecentPosts(PageRequest.of(0, 5)).stream().map(PostListResponse::from).toList();

		List<PostListResponse> commentedPosts = postRepository.findMainCommentedPosts(PageRequest.of(0, 5)).stream().map(PostListResponse::from)
				.toList();

		return MainResponse.builder().noticePosts(noticePosts).popularPosts(popularPosts).recentPosts(recentPosts).commentedPosts(commentedPosts)
				.build();
	}
}