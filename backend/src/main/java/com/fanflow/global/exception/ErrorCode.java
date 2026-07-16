package com.fanflow.global.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// Common
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."), INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

	// Auth / User
	EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."), NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다."),
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."), USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
	USER_NOT_ACTIVE(HttpStatus.FORBIDDEN, "이용할 수 없는 계정입니다."), PASSWORD_NOT_MATCHED(HttpStatus.BAD_REQUEST, "현재 비밀번호가 올바르지 않습니다."),
	CANNOT_BLOCK_SELF(HttpStatus.BAD_REQUEST, "자기 자신은 차단할 수 없습니다."),

	USER_ALREADY_BLOCKED(HttpStatus.CONFLICT, "이미 차단한 사용자입니다."),

	USER_BLOCK_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자 차단 정보를 찾을 수 없습니다."), CANNOT_ACTIVATE_DELETED_USER(HttpStatus.BAD_REQUEST, "탈퇴한 회원은 활성화할 수 없습니다."),

	// Auth
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."), FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

	// channel
	CHANNEL_NOT_FOUND(HttpStatus.NOT_FOUND, "채널을 찾을 수 없습니다."), CHANNEL_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "비활성화된 채널입니다."),
	CHANNEL_SLUG_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 사용 중인 채널 주소입니다."), ALREADY_SUBSCRIBED_CHANNEL(HttpStatus.BAD_REQUEST, "이미 구독한 채널입니다."),
	CHANNEL_SUBSCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "채널 구독 정보를 찾을 수 없습니다."),

	// Channel Member
	CHANNEL_OWNER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 채널 소유자가 지정되어 있습니다."),
	CHANNEL_MEMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 해당 채널의 운영자로 등록된 사용자입니다."),
	CHANNEL_MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "채널 운영자 정보를 찾을 수 없습니다."), CHANNEL_MANAGE_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 채널을 관리할 권한이 없습니다."),
	CHANNEL_MANAGER_NOT_FOUND(HttpStatus.NOT_FOUND, "채널 매니저 정보를 찾을 수 없습니다."),

	// Board / Post
	BOARD_NOT_FOUND(HttpStatus.NOT_FOUND, "게시판을 찾을 수 없습니다."), BOARD_NOT_ACTIVE(HttpStatus.BAD_REQUEST, "비활성화된 게시판입니다."),
	BOARD_CODE_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 사용 중인 게시판 코드입니다."), NOTICE_WRITE_FORBIDDEN(HttpStatus.FORBIDDEN, "공지사항은 관리자만 작성할 수 있습니다."),
	POST_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글을 찾을 수 없습니다."), POST_ACCESS_DENIED(HttpStatus.FORBIDDEN, "게시글에 대한 권한이 없습니다."),

	// Comment
	COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글을 찾을 수 없습니다."), COMMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "댓글에 대한 권한이 없습니다."),
	ALREADY_LIKED_COMMENT(HttpStatus.CONFLICT, "이미 좋아요를 누른 댓글입니다."),

	COMMENT_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "댓글 좋아요 정보를 찾을 수 없습니다."),

	// Like
	ALREADY_LIKED_POST(HttpStatus.BAD_REQUEST, "이미 좋아요를 누른 게시글입니다."), POST_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 정보를 찾을 수 없습니다."),

	// File
	FILE_EMPTY(HttpStatus.BAD_REQUEST, "업로드할 파일이 없습니다."), INVALID_IMAGE_FILE(HttpStatus.BAD_REQUEST, "이미지 파일만 업로드할 수 있습니다."),
	FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

	IMAGE_FILE_EMPTY(HttpStatus.BAD_REQUEST, "이미지 파일이 비어 있습니다."), IMAGE_FILE_TOO_LARGE(HttpStatus.BAD_REQUEST, "이미지 파일은 최대 5MB까지 업로드할 수 있습니다."),
	IMAGE_EXTENSION_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "허용되지 않은 이미지 확장자입니다."),
	IMAGE_CONTENT_TYPE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "허용되지 않은 이미지 형식입니다."),
	IMAGE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다."),

	// Report
	REPORT_TARGET_NOT_FOUND(HttpStatus.NOT_FOUND, "신고 대상을 찾을 수 없습니다."), ALREADY_REPORTED(HttpStatus.BAD_REQUEST, "이미 신고한 대상입니다."),
	REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "신고 내역을 찾을 수 없습니다."), REPORT_ALREADY_RESOLVED(HttpStatus.BAD_REQUEST, "이미 처리된 신고입니다."),

	ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."), NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "알림을 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String message;
}