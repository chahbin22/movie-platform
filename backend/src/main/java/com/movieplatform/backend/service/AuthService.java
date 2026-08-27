package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.auth.SignupRequest;
import com.movieplatform.backend.dto.user.UserResponseDto;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.movieplatform.backend.dto.auth.LoginRequest;
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDto signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        if (userRepository.existsByNickname(request.nickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
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
    public UserResponseDto login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.")
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new IllegalArgumentException(
                    "이메일 또는 비밀번호가 올바르지 않습니다."
            );
        }

        return UserResponseDto.from(user);
    }
}