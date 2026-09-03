package com.movieplatform.backend.dto.schedule;

import com.movieplatform.backend.entity.ScheduleSeat;
import com.movieplatform.backend.entity.ScheduleSeatStatus;

public record ScheduleSeatResponseDto(
        Long scheduleSeatId,
        Long seatId,
        String seatRow,
        Integer seatNumber,
        String seatType,
        ScheduleSeatStatus status,
        Integer price
) {

    public static ScheduleSeatResponseDto from(
            ScheduleSeat scheduleSeat
    ) {
        return new ScheduleSeatResponseDto(
                scheduleSeat.getScheduleSeatId(),
                scheduleSeat.getSeat().getSeatId(),
                scheduleSeat.getSeat().getSeatRow(),
                scheduleSeat.getSeat().getSeatNumber(),
                scheduleSeat.getSeat().getSeatType(),
                scheduleSeat.getStatus(),
                scheduleSeat.getPrice()
        );
    }
}