package com.fanflow.global.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fanflow.global.config.CorsProperties;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
	private final CustomAccessDeniedHandler customAccessDeniedHandler;
	private final CorsProperties corsProperties;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).cors(cors -> cors.configurationSource(corsConfigurationSource())).formLogin(form -> form.disable())
				.httpBasic(basic -> basic.disable()).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.exceptionHandling(exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint).accessDeniedHandler(
						customAccessDeniedHandler))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/", "/api/health", "/api/users/signup", "/api/auth/login", "/api/boards", "/uploads/**", "/swagger-ui/**",
								"/v3/api-docs/**")
						.permitAll().requestMatchers(HttpMethod.GET, "/api/channels/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/channels/*/subscribe").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/channels/*/subscribe").authenticated().requestMatchers("/api/users/me/**")
						.authenticated().requestMatchers(HttpMethod.POST, "/api/posts/images").authenticated()
						.requestMatchers(HttpMethod.GET, "/api/posts/*/likes/me").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/posts/*/likes").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/posts/*/likes").authenticated().requestMatchers(HttpMethod.GET, "/api/posts/**")
						.permitAll().requestMatchers(HttpMethod.POST, "/api/posts").authenticated().requestMatchers(HttpMethod.PUT, "/api/posts/**")
						.authenticated().requestMatchers(HttpMethod.DELETE, "/api/posts/**").authenticated()
						.requestMatchers(HttpMethod.POST, "/api/posts/*/comments").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/api/comments/**").authenticated().requestMatchers(HttpMethod.POST, "/api/reports")
						.authenticated().requestMatchers("/api/notifications/**").authenticated().requestMatchers("/api/users/me").authenticated()
						.requestMatchers("/api/users/me/**").authenticated().requestMatchers("/api/admin/**").hasRole("ADMIN").anyRequest()
						.permitAll())
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOrigins(corsProperties.getAllowedOrigins());
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}
}