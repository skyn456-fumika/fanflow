package com.fanflow.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.fanflow.global.response.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
		ErrorCode errorCode = e.getErrorCode();

		return ResponseEntity.status(errorCode.getStatus()).body(ApiResponse.fail(errorCode.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException e) {
		Map<String, String> errors = new HashMap<>();

		for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
			errors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}

		return ResponseEntity.badRequest().body(ApiResponse.fail("입력값 검증에 실패했습니다.", errors));
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		return ResponseEntity.internalServerError().body(ApiResponse.fail("서버 내부 오류가 발생했습니다."));
	}

	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.fail("이미지 파일은 최대 5MB까지 업로드할 수 있습니다."));
	}
}