package com.movieplatform.backend.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movie Platform API")
                        .description("영화 예매 및 커뮤니티 플랫폼 API")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(
                                SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .name(SECURITY_SCHEME_NAME)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                        )
                );
    }

    @Bean
    public OpenApiCustomizer securityOpenApiCustomizer() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }

            openApi.getPaths().forEach((path, pathItem) -> {

                Map<PathItem.HttpMethod, Operation> operations =
                        pathItem.readOperationsMap();

                operations.forEach((httpMethod, operation) -> {

                    if (requiresAuthentication(path, httpMethod)) {
                        operation.addSecurityItem(
                                new SecurityRequirement()
                                        .addList(SECURITY_SCHEME_NAME)
                        );
                    }
                });
            });
        };
    }

    private boolean requiresAuthentication(
            String path,
            PathItem.HttpMethod method
    ) {

        if (path.equals("/api/users/me")) {
            return true;
        }

        if (path.startsWith("/api/reservations")) {
            return true;
        }

        if (path.equals("/api/posts")
                && method == PathItem.HttpMethod.POST) {
            return true;
        }

        if (path.equals("/api/posts/{postId}")
                && (method == PathItem.HttpMethod.PATCH
                || method == PathItem.HttpMethod.DELETE)) {
            return true;
        }

        if (path.equals("/api/posts/{postId}/comments")
                && method == PathItem.HttpMethod.POST) {
            return true;
        }

        if (path.equals("/api/posts/{postId}/comments/{commentId}")
                && (method == PathItem.HttpMethod.PATCH
                || method == PathItem.HttpMethod.DELETE)) {
            return true;
        }

        if (path.equals("/api/movies/{movieId}/reviews")
                && method == PathItem.HttpMethod.POST) {
            return true;
        }

        if (path.equals("/api/movies/{movieId}/reviews/{reviewId}")
                && (method == PathItem.HttpMethod.PATCH
                || method == PathItem.HttpMethod.DELETE)) {
            return true;
        }

        return false;
    }
}