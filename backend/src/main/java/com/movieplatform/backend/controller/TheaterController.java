package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.theater.TheaterResponseDto;
import com.movieplatform.backend.service.TheaterService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theaters")
public class TheaterController {

    private final TheaterService theaterService;

    public TheaterController(
            TheaterService theaterService
    ) {
        this.theaterService = theaterService;
    }

    @GetMapping
    public List<TheaterResponseDto> getTheaters() {
        return theaterService.getTheaters();
    }

    @GetMapping("/{theaterId}")
    public TheaterResponseDto getTheater(
            @PathVariable Long theaterId
    ) {
        return theaterService.getTheater(theaterId);
    }
}