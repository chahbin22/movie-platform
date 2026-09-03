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

                        // 리뷰 작성 - 로그인 필요
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/movies/*/reviews"
                        )
                        .authenticated()

                        // 리뷰 수정 - 로그인 필요
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/movies/*/reviews/*"
                        )
                        .authenticated()

                        // 리뷰 삭제 - 로그인 필요
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/movies/*/reviews/*"
                        )
                        .authenticated()

                        // 마이페이지 / 예매 관련 - 로그인 필요
                        .requestMatchers(
                                "/api/users/me",
                                "/api/reservations/**"
                        )
                        .authenticated()

                        // 나머지는 로그인 없이 접근 가능
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