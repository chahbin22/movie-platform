package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.schedule.ScheduleSeatResponseDto;
import com.movieplatform.backend.entity.Schedule;
import com.movieplatform.backend.entity.ScheduleSeat;
import com.movieplatform.backend.entity.Seat;
import com.movieplatform.backend.repository.ScheduleRepository;
import com.movieplatform.backend.repository.ScheduleSeatRepository;
import com.movieplatform.backend.repository.SeatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScheduleSeatService {

    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;

    public ScheduleSeatService(
            ScheduleSeatRepository scheduleSeatRepository,
            ScheduleRepository scheduleRepository,
            SeatRepository seatRepository
    ) {
        this.scheduleSeatRepository = scheduleSeatRepository;
        this.scheduleRepository = scheduleRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public List<ScheduleSeatResponseDto> initializeScheduleSeats(
            Long scheduleId
    ) {

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "상영 일정을 찾을 수 없습니다."
                        )
                );

        if (scheduleSeatRepository
                .existsBySchedule_ScheduleId(scheduleId)) {

            return getScheduleSeats(scheduleId);
        }

        Long screenId =
                schedule.getScreen().getScreenId();

        List<Seat> seats =
                seatRepository
                        .findByScreen_ScreenIdOrderBySeatRowAscSeatNumberAsc(
                                screenId
                        );

        if (seats.isEmpty()) {
            throw new IllegalArgumentException(
                    "상영관에 등록된 좌석이 없습니다."
            );
        }

        List<ScheduleSeat> scheduleSeats =
                seats.stream()
                        .map(seat ->
                                new ScheduleSeat(
                                        schedule,
                                        seat,
                                        schedule.getBasePrice()
                                )
                        )
                        .toList();

        return scheduleSeatRepository
                .saveAll(scheduleSeats)
                .stream()
                .map(ScheduleSeatResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ScheduleSeatResponseDto> getScheduleSeats(
            Long scheduleId
    ) {

        return scheduleSeatRepository
                .findBySchedule_ScheduleIdOrderBySeat_SeatRowAscSeat_SeatNumberAsc(
                        scheduleId
                )
                .stream()
                .map(ScheduleSeatResponseDto::from)
                .toList();
    }
}