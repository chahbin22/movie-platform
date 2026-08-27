package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.auth.LoginRequest;
import com.movieplatform.backend.dto.auth.LoginResponse;
import com.movieplatform.backend.dto.auth.SignupRequest;
import com.movieplatform.backend.dto.user.UserResponseDto;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.repository.UserRepository;
import com.movieplatform.backend.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public UserResponseDto signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 이메일입니다."
            );
        }

        if (userRepository.existsByNickname(request.nickname())) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 닉네임입니다."
            );
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

        User user = new User(
                request.email(),
                encodedPassword,
                request.nickname()
        );

        User savedUser = userRepository.save(user);

        return UserResponseDto.from(savedUser);
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "이메일 또는 비밀번호가 올바르지 않습니다."
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        String accessToken = jwtProvider.createToken(
                user.getUserId(),
                user.getEmail()
        );

        return new LoginResponse(
                accessToken,
                UserResponseDto.from(user)
        );
    }
}