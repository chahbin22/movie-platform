package com.movieplatform.backend.dto.reservation;

import com.movieplatform.backend.entity.Reservation;
import com.movieplatform.backend.entity.ReservationSeat;
import com.movieplatform.backend.entity.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponseDto(
        Long reservationId,
        ReservationStatus status,
        Integer totalPrice,

        Long scheduleId,
        Long movieId,
        String movieTitle,

        Long theaterId,
        String theaterName,

        Long screenId,
        String screenName,

        LocalDateTime startTime,
        LocalDateTime createdAt,

        List<ReservationSeatResponseDto> seats
) {

    public static ReservationResponseDto from(
            Reservation reservation,
            List<ReservationSeat> reservationSeats
    ) {
        return new ReservationResponseDto(
                reservation.getReservationId(),
                reservation.getStatus(),
                reservation.getTotalPrice(),

                reservation.getSchedule().getScheduleId(),

                reservation
                        .getSchedule()
                        .getMovie()
                        .getMovieId(),

                reservation
                        .getSchedule()
                        .getMovie()
                        .getTitle(),

                reservation
                        .getSchedule()
                        .getScreen()
                        .getTheater()
                        .getTheaterId(),

                reservation
                        .getSchedule()
                        .getScreen()
                        .getTheater()
                        .getName(),

                reservation
                        .getSchedule()
                        .getScreen()
                        .getScreenId(),

                reservation
                        .getSchedule()
                        .getScreen()
                        .getName(),

                reservation.getSchedule().getStartTime(),
                reservation.getCreatedAt(),

                reservationSeats.stream()
                        .map(ReservationSeatResponseDto::from)
                        .toList()
        );
    }
}