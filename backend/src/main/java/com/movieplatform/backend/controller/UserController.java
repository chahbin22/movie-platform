package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.user.UserResponseDto;
import com.movieplatform.backend.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(
            UserService userService
    ) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserResponseDto getMe(
            Authentication authentication
    ) {
        Long userId =
                (Long) authentication.getPrincipal();

        return userService.getMe(userId);
    }
}