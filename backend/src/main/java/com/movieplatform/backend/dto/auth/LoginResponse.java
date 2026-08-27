package com.movieplatform.backend.dto.auth;

import com.movieplatform.backend.dto.user.UserResponseDto;

public record LoginResponse(
        String accessToken,
        UserResponseDto user
) {
}