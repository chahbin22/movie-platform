package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.schedule.ScheduleResponseDto;
import com.movieplatform.backend.repository.ScheduleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(
            ScheduleRepository scheduleRepository
    ) {
        this.scheduleRepository = scheduleRepository;
    }

    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getSchedules(
            Long movieId,
            Long theaterId,
            LocalDate date
    ) {

        LocalDateTime start =
                date.atStartOfDay();

        LocalDateTime end =
                date.plusDays(1).atStartOfDay();

        return scheduleRepository
                .findByMovie_MovieIdAndScreen_Theater_TheaterIdAndStartTimeBetweenOrderByStartTimeAsc(
                        movieId,
                        theaterId,
                        start,
                        end
                )
                .stream()
                .map(ScheduleResponseDto::from)
                .toList();
    }
}