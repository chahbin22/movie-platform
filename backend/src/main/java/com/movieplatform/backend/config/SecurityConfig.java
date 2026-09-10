package com.movieplatform.backend.config;

import com.movieplatform.backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String frontendUrl;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${FRONTEND_URL:http://localhost:5173}")
            String frontendUrl
    ) {
        this.jwtAuthenticationFilter =
                jwtAuthenticationFilter;

        this.frontendUrl =
                frontendUrl;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                // Frontend -> Backend CORS 허용
                .cors(Customizer.withDefaults())

                // JWT 방식이므로 CSRF 비활성화
                .csrf(csrf -> csrf.disable())

                // 기본 로그인 화면 사용하지 않음
                .formLogin(form -> form.disable())

                // HTTP Basic 인증 사용하지 않음
                .httpBasic(basic -> basic.disable())

                // JWT를 사용하므로 세션을 생성하지 않음
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // 인증 실패 시 401 반환
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(
                                                HttpServletResponse
                                                        .SC_UNAUTHORIZED
                                        )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // Review
                        // =========================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/movies/*/reviews"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/movies/*/reviews/*"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/movies/*/reviews/*"
                        )
                        .authenticated()


                        // =========================
                        // Post
                        // =========================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/posts"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/posts/*"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/posts/*"
                        )
                        .authenticated()


                        // =========================
                        // Comment
                        // =========================

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/posts/*/comments"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/posts/*/comments/*"
                        )
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/posts/*/comments/*"
                        )
                        .authenticated()


                        // =========================
                        // User / Reservation
                        // =========================

                        .requestMatchers(
                                "/api/users/me",
                                "/api/reservations/**"
                        )
                        .authenticated()


                        // =========================
                        // Public API
                        // =========================

                        .anyRequest()
                        .permitAll()
                )

                // JWT 필터를 기본 인증 필터보다 먼저 실행
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource
    corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        configuration.setAllowedOrigins(
                List.of(frontendUrl)
        );

        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PATCH",
                        "DELETE",
                        "OPTIONS"
                )
        );

        configuration.setAllowedHeaders(
                List.of(
                        "Authorization",
                        "Content-Type"
                )
        );

        configuration.setExposedHeaders(
                List.of(
                        "Authorization"
                )
        );

        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}