package com.moin.remittance.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("ver.1.0.0")
                .title("기업 과제 테스트: 📚 모인 백엔드 API ")
                .description("해외 송금앱 백엔드 서버: 수수료 정책 적용");


        // Define JWT Bearer token security scheme
        SecurityScheme securityScheme = new SecurityScheme()
                .name("Bearer Authentication")
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme)) // Add the security scheme globally
                .info(info)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));  // Apply security globally to all APIs
    }


}
