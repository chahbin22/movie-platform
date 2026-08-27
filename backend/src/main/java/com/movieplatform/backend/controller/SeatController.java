package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.seat.SeatResponseDto;
import com.movieplatform.backend.service.SeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class SeatController {

    private final SeatService seatService;

    public SeatController(
            SeatService seatService
    ) {
        this.seatService = seatService;
    }

    @GetMapping("/api/screens/{screenId}/seats")
    public List<SeatResponseDto> getSeatsByScreen(
            @PathVariable Long screenId
    ) {
        return seatService.getSeatsByScreen(screenId);
    }

    @GetMapping("/api/seats/{seatId}")
    public SeatResponseDto getSeat(
            @PathVariable Long seatId
    ) {
        return seatService.getSeat(seatId);
    }
}