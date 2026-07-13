package com.fanflow.domain.user;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fanflow.domain.comment.dto.CommentResponse;
import com.fanflow.domain.post.dto.PostListResponse;
import com.fanflow.domain.user.dto.NicknameUpdateRequest;
import com.fanflow.domain.user.dto.PasswordUpdateRequest;
import com.fanflow.domain.user.dto.ProfileImageUpdateResponse;
import com.fanflow.domain.user.dto.SignupRequest;
import com.fanflow.domain.user.dto.UserDeleteRequest;
import com.fanflow.domain.user.dto.UserResponse;
import com.fanflow.global.response.ApiResponse;
import com.fanflow.global.response.PageResponse;
import com.fanflow.global.security.CurrentUser;
import com.fanflow.global.security.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@PostMapping("/api/users/signup")
	public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest request) {
		UserResponse response = userService.signup(request);
		return ApiResponse.success("회원가입이 완료되었습니다.", response);
	}

	@GetMapping("/api/users/me")
	public ApiResponse<UserResponse> me(@CurrentUser CustomUserDetails userDetails) {
		UserResponse response = UserResponse.from(userDetails.getUser());
		return ApiResponse.success("내 정보 조회에 성공했습니다.", response);
	}

	@PatchMapping("/api/users/me/nickname")
	public ApiResponse<UserResponse> updateNickname(@CurrentUser CustomUserDetails userDetails, @Valid @RequestBody NicknameUpdateRequest request) {
		UserResponse response = userService.updateNickname(userDetails.getUserId(), request);

		return ApiResponse.success("닉네임이 변경되었습니다.", response);
	}

	@PatchMapping("/api/users/me/password")
	public ApiResponse<Void> updatePassword(@CurrentUser CustomUserDetails userDetails, @Valid @RequestBody PasswordUpdateRequest request) {
		userService.updatePassword(userDetails.getUserId(), request);

		return ApiResponse.success("비밀번호가 변경되었습니다.");
	}

	@GetMapping("/api/users/me/posts")
	public ApiResponse<PageResponse<PostListResponse>> getMyPosts(@CurrentUser CustomUserDetails userDetails,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<PostListResponse> response = userService.getMyPosts(userDetails.getUserId(), page, size);

		return ApiResponse.success("내 게시글 목록 조회에 성공했습니다.", response);
	}

	@GetMapping("/api/users/me/comments")
	public ApiResponse<PageResponse<CommentResponse>> getMyComments(@CurrentUser CustomUserDetails userDetails,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<CommentResponse> response = userService.getMyComments(userDetails.getUserId(), page, size);

		return ApiResponse.success("내 댓글 목록 조회에 성공했습니다.", response);
	}

	@GetMapping("/api/users/me/likes")
	public ApiResponse<PageResponse<PostListResponse>> getMyLikedPosts(@CurrentUser CustomUserDetails userDetails,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		PageResponse<PostListResponse> response = userService.getMyLikedPosts(userDetails.getUserId(), page, size);

		return ApiResponse.success("내가 좋아요한 게시글 목록 조회에 성공했습니다.", response);
	}

	@DeleteMapping("/api/users/me")
	public ApiResponse<Void> deleteMe(@CurrentUser CustomUserDetails userDetails, @Valid @RequestBody UserDeleteRequest request) {
		userService.deleteMe(userDetails.getUserId(), request);
		return ApiResponse.success("회원 탈퇴가 완료되었습니다.");
	}

	@PostMapping("/api/users/me/profile-image")
	public ApiResponse<ProfileImageUpdateResponse> updateProfileImage(@CurrentUser CustomUserDetails userDetails,
			@RequestParam("file") MultipartFile file) {
		ProfileImageUpdateResponse response = userService.updateProfileImage(userDetails.getUserId(), file);

		return ApiResponse.success("프로필 이미지가 변경되었습니다.", response);
	}
}