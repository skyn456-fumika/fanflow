package com.fanflow.global.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fanflow.domain.user.User;
import com.fanflow.domain.user.UserRole;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	private final SecretKey secretKey;
	private final long expiration;

	public JwtProvider(@Value("${jwt.secret}") String secret, @Value("${jwt.expiration}") long expiration) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expiration = expiration;
	}

	public String createAccessToken(User user) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);

		return Jwts.builder().subject(String.valueOf(user.getUserId())).claim("email", user.getEmail()).claim("nickname", user.getNickname())
				.claim("role", user.getRole().name()).issuedAt(now).expiration(expiryDate).signWith(secretKey).compact();
	}

	public Long getUserId(String token) {
		Claims claims = parseClaims(token);
		return Long.valueOf(claims.getSubject());
	}

	public String getEmail(String token) {
		Claims claims = parseClaims(token);
		return claims.get("email", String.class);
	}

	public UserRole getRole(String token) {
		Claims claims = parseClaims(token);
		return UserRole.valueOf(claims.get("role", String.class));
	}

	public boolean validateToken(String token) {
		try {
			parseClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private Claims parseClaims(String token) {
		return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
	}
}