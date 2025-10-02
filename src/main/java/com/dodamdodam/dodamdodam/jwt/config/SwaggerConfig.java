package com.dodamdodam.dodamdodam.jwt.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import org.springframework.context.annotation.Configuration;

// Swagger UI에 대한 기본 정보를 설정합니다.
@OpenAPIDefinition(
        info = @Info(title = "도담도담 API 명세서",
                description = "독서 기록 및 퀴즈 API 명세서입니다.",
                version = "v1.0.0"),
        // ✅ 모든 API에 전역적으로 보안 요구사항을 추가합니다.
        security = @SecurityRequirement(name = "bearerAuth")
)
// ✅ 'bearerAuth'라는 이름의 보안 스킴(Security Scheme)을 정의합니다.
@SecurityScheme(
        name = "bearerAuth", // 보안 스킴의 이름을 지정합니다.
        type = SecuritySchemeType.HTTP, // 보안 스킴의 타입으로 HTTP를 지정합니다.
        scheme = "bearer", // HTTP 스킴으로 'bearer'를 사용한다고 명시합니다.
        bearerFormat = "JWT" // 베어러 토큰의 형식으로 JWT를 사용한다고 명시합니다.
)
@Configuration
public class SwaggerConfig {
}