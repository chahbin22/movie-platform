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
                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())

                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(
                                (request, response, authException) ->
                                        response.sendError(
                                                HttpServletResponse.SC_UNAUTHORIZED
                                        )
                        )
                )

                .authorizeHttpRequests(auth -> auth

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

                        // 내 정보 + 예매 관련
                        .requestMatchers(
                                "/api/users/me",
                                "/api/reservations/**"
                        )
                        .authenticated()

                        // 나머지 API 공개
                        .anyRequest()
                        .permitAll()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}