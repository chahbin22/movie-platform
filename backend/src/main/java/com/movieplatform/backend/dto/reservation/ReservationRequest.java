package com.movieplatform.backend.dto.reservation;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record ReservationRequest(

        @NotNull
        Long scheduleId,

        @NotEmpty
        List<Long> scheduleSeatIds

) {
}