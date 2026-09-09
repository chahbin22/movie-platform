package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository
        extends JpaRepository<Seat, Long> {

    List<Seat>
    findByScreen_ScreenIdOrderBySeatRowAscSeatNumberAsc(
            Long screenId
    );

    boolean
    existsByScreen_ScreenIdAndSeatRowAndSeatNumber(
            Long screenId,
            String seatRow,
            Integer seatNumber
    );
}