package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.schedule.ScheduleResponseDto;
import com.movieplatform.backend.service.ScheduleService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(
            ScheduleService scheduleService
    ) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<ScheduleResponseDto> getSchedules(
            @RequestParam Long movieId,
            @RequestParam Long theaterId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return scheduleService.getSchedules(
                movieId,
                theaterId,
                date
        );
    }
}