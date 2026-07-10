package com.fanflow.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration
public class SwaggerConfig {

	private static final String SECURITY_SCHEME_NAME = "bearerAuth";

	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI().info(apiInfo())
				.components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
						new SecurityScheme().name(SECURITY_SCHEME_NAME).type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
	}

	private Info apiInfo() {
		return new Info().title("FanFlow API").description("스트리머 팬 커뮤니티 FanFlow API 문서").version("1.0.0");
	}
}