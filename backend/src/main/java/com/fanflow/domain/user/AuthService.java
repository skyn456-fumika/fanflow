package com.fanflow.domain.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.user.dto.LoginRequest;
import com.fanflow.domain.user.dto.TokenResponse;
import com.fanflow.global.exception.BusinessException;
import com.fanflow.global.exception.ErrorCode;
import com.fanflow.global.security.JwtProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtProvider jwtProvider;

	public TokenResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BusinessException(ErrorCode.LOGIN_FAILED));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BusinessException(ErrorCode.LOGIN_FAILED);
		}

		if (!user.isActive()) {
			throw new BusinessException(ErrorCode.USER_NOT_ACTIVE);
		}

		String accessToken = jwtProvider.createAccessToken(user);

		return TokenResponse.of(accessToken);
	}
}