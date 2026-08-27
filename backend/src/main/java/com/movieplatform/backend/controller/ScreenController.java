package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.screen.ScreenResponseDto;
import com.movieplatform.backend.service.ScreenService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ScreenController {

    private final ScreenService screenService;

    public ScreenController(
            ScreenService screenService
    ) {
        this.screenService = screenService;
    }

    @GetMapping("/api/theaters/{theaterId}/screens")
    public List<ScreenResponseDto> getScreensByTheater(
            @PathVariable Long theaterId
    ) {
        return screenService.getScreensByTheater(theaterId);
    }

    @GetMapping("/api/screens/{screenId}")
    public ScreenResponseDto getScreen(
            @PathVariable Long screenId
    ) {
        return screenService.getScreen(screenId);
    }
}