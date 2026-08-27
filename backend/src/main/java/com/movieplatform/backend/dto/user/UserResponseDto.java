package com.movieplatform.backend.dto.user;

import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.entity.UserRole;

public record UserResponseDto(
        Long userId,
        String email,
        String nickname,
        UserRole role
) {

    public static UserResponseDto from(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole()
        );
    }
}