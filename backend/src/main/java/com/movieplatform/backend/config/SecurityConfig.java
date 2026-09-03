package com.movieplatform.backend.config;

import com.movieplatform.backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
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
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        )
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // =========================
                        // Review
                        // =========================

                        // 리뷰 작성
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/movies/*/reviews"
                        )
                        .authenticated()

                        // 리뷰 수정
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/movies/*/reviews/*"
                        )
                        .authenticated()

                        // 리뷰 삭제
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/movies/*/reviews/*"
                        )
                        .authenticated()


                        // =========================
                        // Post
                        // =========================

                        // 게시글 작성
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/posts"
                        )
                        .authenticated()

                        // 게시글 수정
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/posts/*"
                        )
                        .authenticated()

                        // 게시글 삭제
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/posts/*"
                        )
                        .authenticated()


                        // =========================
                        // Comment
                        // =========================

                        // 댓글 작성
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/posts/*/comments"
                        )
                        .authenticated()

                        // 댓글 수정
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/posts/*/comments/*"
                        )
                        .authenticated()

                        // 댓글 삭제
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/posts/*/comments/*"
                        )
                        .authenticated()


                        // =========================
                        // User / Reservation
                        // =========================

                        // 내 정보 조회 + 모든 예매 관련 API
                        .requestMatchers(
                                "/api/users/me",
                                "/api/reservations/**"
                        )
                        .authenticated()


                        // =========================
                        // Public API
                        // =========================

                        // 그 외 API는 로그인 없이 접근 가능
                        .anyRequest()
                        .permitAll()
                )

                // JWT 필터를 Spring Security 기본 인증 필터보다 먼저 실행
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}