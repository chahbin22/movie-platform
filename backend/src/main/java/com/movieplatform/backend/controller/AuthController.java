package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.auth.SignupRequest;
import com.movieplatform.backend.dto.user.UserResponseDto;
import com.movieplatform.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public UserResponseDto signup(
            @Valid @RequestBody SignupRequest request
    ) {
        return authService.signup(request);
    }
}