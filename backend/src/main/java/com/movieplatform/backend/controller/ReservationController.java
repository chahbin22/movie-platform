package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.reservation.ReservationRequest;
import com.movieplatform.backend.dto.reservation.ReservationResponseDto;
import com.movieplatform.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(
            ReservationService reservationService
    ) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ReservationResponseDto createReservation(
            Authentication authentication,
            @Valid @RequestBody ReservationRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return reservationService.createReservation(
                userId,
                request
        );
    }
    @GetMapping("/me")
        public List<ReservationResponseDto> getMyReservations(
                Authentication authentication
        ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return reservationService
                .getMyReservations(userId);
        }
    @PatchMapping("/{reservationId}/cancel")
        public ReservationResponseDto cancelReservation(
                Authentication authentication,
                @PathVariable Long reservationId
        ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return reservationService
                .cancelReservation(
                        userId,
                        reservationId
                );
        }
}