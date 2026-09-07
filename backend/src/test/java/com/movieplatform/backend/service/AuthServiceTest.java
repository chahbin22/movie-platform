package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.auth.LoginRequest;
import com.movieplatform.backend.dto.auth.LoginResponse;
import com.movieplatform.backend.dto.auth.SignupRequest;
import com.movieplatform.backend.dto.user.UserResponseDto;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.repository.UserRepository;
import com.movieplatform.backend.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtProvider
        );
    }

    @Test
    void 회원가입에_성공한다() {

        SignupRequest request = new SignupRequest(
                "test@example.com",
                "password1234",
                "테스트유저"
        );

        User savedUser = mock(User.class);

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(userRepository.existsByNickname(request.nickname()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        UserResponseDto result =
                authService.signup(request);

        assertNotNull(result);

        verify(userRepository)
                .existsByEmail(request.email());

        verify(userRepository)
                .existsByNickname(request.nickname());

        verify(passwordEncoder)
                .encode(request.password());

        verify(userRepository)
                .save(any(User.class));
    }

    @Test
    void 중복된_이메일이면_회원가입할_수_없다() {

        SignupRequest request = new SignupRequest(
                "test@example.com",
                "password1234",
                "테스트유저"
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.signup(request)
        );

        assertEquals(
                "이미 사용 중인 이메일입니다.",
                exception.getMessage()
        );

        verify(userRepository)
                .existsByEmail(request.email());

        verify(userRepository, never())
                .existsByNickname(anyString());

        verifyNoInteractions(passwordEncoder);

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void 중복된_닉네임이면_회원가입할_수_없다() {

        SignupRequest request = new SignupRequest(
                "test@example.com",
                "password1234",
                "테스트유저"
        );

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(userRepository.existsByNickname(request.nickname()))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.signup(request)
        );

        assertEquals(
                "이미 사용 중인 닉네임입니다.",
                exception.getMessage()
        );

        verifyNoInteractions(passwordEncoder);

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void 회원가입할_때_비밀번호를_암호화해서_저장한다() {

        SignupRequest request = new SignupRequest(
                "test@example.com",
                "password1234",
                "테스트유저"
        );

        User savedUser = mock(User.class);

        when(userRepository.existsByEmail(request.email()))
                .thenReturn(false);

        when(userRepository.existsByNickname(request.nickname()))
                .thenReturn(false);

        when(passwordEncoder.encode(request.password()))
                .thenReturn("encoded-password");

        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);

        authService.signup(request);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertEquals(
                "test@example.com",
                capturedUser.getEmail()
        );

        assertEquals(
                "encoded-password",
                capturedUser.getPassword()
        );

        assertEquals(
                "테스트유저",
                capturedUser.getNickname()
        );
    }

    @Test
    void 로그인에_성공하면_AccessToken을_반환한다() {

        LoginRequest request = new LoginRequest(
                "test@example.com",
                "password1234"
        );

        User user = mock(User.class);

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        when(user.getPassword())
                .thenReturn("encoded-password");

        when(passwordEncoder.matches(
                request.password(),
                "encoded-password"
        )).thenReturn(true);

        when(user.getUserId())
                .thenReturn(1L);

        when(user.getEmail())
                .thenReturn("test@example.com");

        when(jwtProvider.createToken(
                1L,
                "test@example.com"
        )).thenReturn("test-access-token");

        LoginResponse result =
                authService.login(request);

        assertNotNull(result);

        assertEquals(
                "test-access-token",
                result.accessToken()
        );

        verify(jwtProvider)
                .createToken(
                        1L,
                        "test@example.com"
                );
    }

    @Test
    void 존재하지_않는_이메일로_로그인하면_실패한다() {

        LoginRequest request = new LoginRequest(
                "none@example.com",
                "password1234"
        );

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals(
                "이메일 또는 비밀번호가 올바르지 않습니다.",
                exception.getMessage()
        );

        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtProvider);
    }

    @Test
    void 비밀번호가_틀리면_로그인할_수_없다() {

        LoginRequest request = new LoginRequest(
                "test@example.com",
                "wrong-password"
        );

        User user = mock(User.class);

        when(userRepository.findByEmail(request.email()))
                .thenReturn(Optional.of(user));

        when(user.getPassword())
                .thenReturn("encoded-password");

        when(passwordEncoder.matches(
                request.password(),
                "encoded-password"
        )).thenReturn(false);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> authService.login(request)
        );

        assertEquals(
                "이메일 또는 비밀번호가 올바르지 않습니다.",
                exception.getMessage()
        );

        verifyNoInteractions(jwtProvider);
    }
}