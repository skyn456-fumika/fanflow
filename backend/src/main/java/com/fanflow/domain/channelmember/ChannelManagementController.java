package com.fanflow.domain.channelmember;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fanflow.domain.board.BoardService;
import com.fanflow.domain.board.dto.BoardCreateRequest;
import com.fanflow.domain.board.dto.BoardResponse;
import com.fanflow.domain.board.dto.BoardUpdateRequest;
import com.fanflow.domain.channel.Channel;
import com.fanflow.domain.channel.ChannelService;
import com.fanflow.domain.channel.dto.ChannelOwnerUpdateRequest;
import com.fanflow.domain.channel.dto.ChannelResponse;
import com.fanflow.domain.channelmember.dto.ChannelManagerAssignRequest;
import com.fanflow.domain.channelmember.dto.ChannelManagerCandidateResponse;
import com.fanflow.domain.channelmember.dto.ChannelMemberResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChannelManagementController {

	private final ChannelMemberService channelMemberService;
	private final ChannelService channelService;
	private final BoardService boardService;

	@GetMapping("/api/channel-management/channels/{slug}/members")
	public ApiResponse<List<ChannelMemberResponse>> getMembers(@PathVariable String slug, @CurrentUser CustomUserDetails userDetails) {
		Channel channel = channelMemberService.getActiveChannel(slug);

		channelMemberService.validateOwner(channel.getChannelId(), userDetails.getUserId());

		return ApiResponse.success("채널 운영자 목록 조회에 성공했습니다.", channelMemberService.getChannelMembers(channel.getChannelId()));
	}

	@GetMapping("/api/channel-management/channels/{slug}/manager-candidates")
	public ApiResponse<List<ChannelManagerCandidateResponse>> searchCandidates(@PathVariable String slug, @RequestParam String keyword,
			@CurrentUser CustomUserDetails userDetails) {
		Channel channel = channelMemberService.getActiveChannel(slug);

		return ApiResponse.success("매니저 후보 검색에 성공했습니다.",
				channelMemberService.searchManagerCandidates(channel.getChannelId(), userDetails.getUserId(), keyword));
	}

	@PostMapping("/api/channel-management/channels/{slug}/managers")
	public ApiResponse<ChannelMemberResponse> assignManager(@PathVariable String slug, @Valid @RequestBody ChannelManagerAssignRequest request,
			@CurrentUser CustomUserDetails userDetails) {
		Channel channel = channelMemberService.getActiveChannel(slug);

		channelMemberService.validateOwner(channel.getChannelId(), userDetails.getUserId());

		return ApiResponse.success("채널 매니저가 지정되었습니다.", channelMemberService.assignManager(channel.getChannelId(), request.getUserId()));
	}

	@DeleteMapping("/api/channel-management/channels/{slug}/managers/{userId}")
	public ApiResponse<Void> removeManager(@PathVariable String slug, @PathVariable Long userId, @CurrentUser CustomUserDetails userDetails) {
		Channel channel = channelMemberService.getActiveChannel(slug);

		channelMemberService.validateOwner(channel.getChannelId(), userDetails.getUserId());

		channelMemberService.removeManager(channel.getChannelId(), userId);

		return ApiResponse.success("채널 매니저가 해제되었습니다.");
	}

	@GetMapping("/api/channel-management/channels/{slug}")
	public ApiResponse<ChannelResponse> getManagedChannel(@PathVariable String slug, @CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("채널 관리 정보 조회에 성공했습니다.", channelService.getManagedChannel(slug, userDetails.getUserId()));
	}

	@PutMapping("/api/channel-management/channels/{slug}")
	public ApiResponse<ChannelResponse> updateManagedChannel(@PathVariable String slug, @Valid @RequestBody ChannelOwnerUpdateRequest request,
			@CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("채널 정보가 수정되었습니다.", channelService.updateManagedChannel(slug, userDetails.getUserId(), request));
	}

	@PostMapping("/api/channel-management/channels/{slug}/profile-image")
	public ApiResponse<ChannelResponse> uploadManagedProfileImage(@PathVariable String slug, @RequestParam("file") MultipartFile file,
			@CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("채널 프로필 이미지가 업로드되었습니다.", channelService.uploadManagedProfileImage(slug, userDetails.getUserId(), file));
	}

	@PostMapping("/api/channel-management/channels/{slug}/banner-image")
	public ApiResponse<ChannelResponse> uploadManagedBannerImage(@PathVariable String slug, @RequestParam("file") MultipartFile file,
			@CurrentUser CustomUserDetails userDetails) {
		return ApiResponse.success("채널 배너 이미지가 업로드되었습니다.", channelService.uploadManagedBannerImage(slug, userDetails.getUserId(), file));
	}

	private Channel validateOwnerChannel(String slug, Long userId) {
		Channel channel = channelMemberService.getActiveChannel(slug);

		channelMemberService.validateOwner(channel.getChannelId(), userId);

		return channel;
	}

	@GetMapping("/api/channel-management/channels/{slug}/boards")
	public ApiResponse<List<BoardResponse>> getManagedBoards(@PathVariable String slug, @CurrentUser CustomUserDetails userDetails) {
		Channel channel = validateOwnerChannel(slug, userDetails.getUserId());

		return ApiResponse.success("채널 게시판 목록 조회에 성공했습니다.", boardService.getAdminBoardsByChannel(channel.getChannelId()));
	}

	@PostMapping("/api/channel-management/channels/{slug}/boards")
	public ApiResponse<BoardResponse> createManagedBoard(@PathVariable String slug, @Valid @RequestBody BoardCreateRequest request,
			@CurrentUser CustomUserDetails userDetails) {
		Channel channel = validateOwnerChannel(slug, userDetails.getUserId());

		return ApiResponse.success("게시판이 생성되었습니다.", boardService.createBoard(channel.getChannelId(), request));
	}

	@PutMapping("/api/channel-management/channels/{slug}/boards/{boardId}")
	public ApiResponse<BoardResponse> updateManagedBoard(@PathVariable String slug, @PathVariable Long boardId,
			@Valid @RequestBody BoardUpdateRequest request, @CurrentUser CustomUserDetails userDetails) {
		Channel channel = validateOwnerChannel(slug, userDetails.getUserId());

		return ApiResponse.success("게시판이 수정되었습니다.", boardService.updateBoard(channel.getChannelId(), boardId, request));
	}

	@PatchMapping("/api/channel-management/channels/{slug}/boards/{boardId}/activate")
	public ApiResponse<Void> activateManagedBoard(@PathVariable String slug, @PathVariable Long boardId, @CurrentUser CustomUserDetails userDetails) {
		Channel channel = validateOwnerChannel(slug, userDetails.getUserId());

		boardService.activateBoard(channel.getChannelId(), boardId);

		return ApiResponse.success("게시판이 활성화되었습니다.");
	}

	@PatchMapping("/api/channel-management/channels/{slug}/boards/{boardId}/deactivate")
	public ApiResponse<Void> deactivateManagedBoard(@PathVariable String slug, @PathVariable Long boardId,
			@CurrentUser CustomUserDetails userDetails) {
		Channel channel = validateOwnerChannel(slug, userDetails.getUserId());

		boardService.deactivateBoard(channel.getChannelId(), boardId);

		return ApiResponse.success("게시판이 비활성화되었습니다.");
	}
}