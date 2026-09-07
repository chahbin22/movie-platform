package com.movieplatform.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {

        String testSecret =
                Base64.getEncoder()
                        .encodeToString(
                                "01234567890123456789012345678901"
                                        .getBytes(StandardCharsets.UTF_8)
                        );

        jwtProvider = new JwtProvider(
                testSecret,
                3600000L
        );
    }

    @Test
    void JWT를_생성할_수_있다() {

        Long userId = 1L;
        String email = "test@example.com";

        String token =
                jwtProvider.createToken(
                        userId,
                        email
                );

        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void 정상적인_JWT는_검증에_성공한다() {

        String token =
                jwtProvider.createToken(
                        1L,
                        "test@example.com"
                );

        boolean result =
                jwtProvider.validateToken(token);

        assertTrue(result);
    }

    @Test
    void JWT에서_userId를_추출할_수_있다() {

        Long userId = 123L;

        String token =
                jwtProvider.createToken(
                        userId,
                        "test@example.com"
                );

        Long extractedUserId =
                jwtProvider.getUserId(token);

        assertEquals(
                userId,
                extractedUserId
        );
    }

    @Test
    void 변조된_JWT는_검증에_실패한다() {

        String token =
                jwtProvider.createToken(
                        1L,
                        "test@example.com"
                );

        String tamperedToken =
                token + "tampered";

        boolean result =
                jwtProvider.validateToken(tamperedToken);

        assertFalse(result);
    }

    @Test
    void 다른_Secret으로_만든_JWT는_검증에_실패한다() {

        String anotherSecret =
                Base64.getEncoder()
                        .encodeToString(
                                "abcdefghijklmnopqrstuvwxyz123456"
                                        .getBytes(StandardCharsets.UTF_8)
                        );

        JwtProvider anotherJwtProvider =
                new JwtProvider(
                        anotherSecret,
                        3600000L
                );

        String token =
                jwtProvider.createToken(
                        1L,
                        "test@example.com"
                );

        boolean result =
                anotherJwtProvider.validateToken(token);

        assertFalse(result);
    }

    @Test
    void 만료된_JWT는_검증에_실패한다() throws InterruptedException {

        String testSecret =
                Base64.getEncoder()
                        .encodeToString(
                                "01234567890123456789012345678901"
                                        .getBytes(StandardCharsets.UTF_8)
                        );

        JwtProvider shortExpirationProvider =
                new JwtProvider(
                        testSecret,
                        1L
                );

        String token =
                shortExpirationProvider.createToken(
                        1L,
                        "test@example.com"
                );

        Thread.sleep(10);

        boolean result =
                shortExpirationProvider.validateToken(token);

        assertFalse(result);
    }
}