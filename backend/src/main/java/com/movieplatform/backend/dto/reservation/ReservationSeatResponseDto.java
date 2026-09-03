package com.movieplatform.backend.dto.reservation;

import com.movieplatform.backend.entity.ReservationSeat;

public record ReservationSeatResponseDto(
        Long reservationSeatId,
        Long scheduleSeatId,
        Long seatId,
        String seatRow,
        Integer seatNumber,
        String seatType,
        Integer bookedPrice
) {

    public static ReservationSeatResponseDto from(
            ReservationSeat reservationSeat
    ) {
        return new ReservationSeatResponseDto(
                reservationSeat.getReservationSeatId(),

                reservationSeat
                        .getScheduleSeat()
                        .getScheduleSeatId(),

                reservationSeat
                        .getScheduleSeat()
                        .getSeat()
                        .getSeatId(),

                reservationSeat
                        .getScheduleSeat()
                        .getSeat()
                        .getSeatRow(),

                reservationSeat
                        .getScheduleSeat()
                        .getSeat()
                        .getSeatNumber(),

                reservationSeat
                        .getScheduleSeat()
                        .getSeat()
                        .getSeatType(),

                reservationSeat.getBookedPrice()
        );
    }
}