package com.fanflow.global.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		return new CustomUserDetails(user);
	}

	public UserDetails loadUserByUserId(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

		return new CustomUserDetails(user);
	}
}