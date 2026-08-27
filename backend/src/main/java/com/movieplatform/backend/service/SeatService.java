package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.seat.SeatResponseDto;
import com.movieplatform.backend.entity.Seat;
import com.movieplatform.backend.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SeatService {

    private final SeatRepository seatRepository;

    public SeatService(
            SeatRepository seatRepository
    ) {
        this.seatRepository = seatRepository;
    }

    @Transactional(readOnly = true)
    public List<SeatResponseDto> getSeatsByScreen(
            Long screenId
    ) {
        return seatRepository
                .findByScreen_ScreenIdOrderBySeatRowAscSeatNumberAsc(
                        screenId
                )
                .stream()
                .map(SeatResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public SeatResponseDto getSeat(Long seatId) {

        Seat seat = seatRepository.findById(seatId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "좌석을 찾을 수 없습니다."
                        )
                );

        return SeatResponseDto.from(seat);
    }
}