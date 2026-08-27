package com.movieplatform.backend.dto.seat;

import com.movieplatform.backend.entity.Seat;

public record SeatResponseDto(
        Long seatId,
        String seatRow,
        Integer seatNumber,
        String seatType,
        Long screenId
) {

    public static SeatResponseDto from(Seat seat) {
        return new SeatResponseDto(
                seat.getSeatId(),
                seat.getSeatRow(),
                seat.getSeatNumber(),
                seat.getSeatType(),
                seat.getScreen().getScreenId()
        );
    }
}