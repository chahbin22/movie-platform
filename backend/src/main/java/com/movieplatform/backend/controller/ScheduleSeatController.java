package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.schedule.ScheduleSeatResponseDto;
import com.movieplatform.backend.service.ScheduleSeatService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules/{scheduleId}/seats")
public class ScheduleSeatController {

    private final ScheduleSeatService scheduleSeatService;

    public ScheduleSeatController(
            ScheduleSeatService scheduleSeatService
    ) {
        this.scheduleSeatService = scheduleSeatService;
    }

    @PostMapping("/initialize")
    public List<ScheduleSeatResponseDto> initializeScheduleSeats(
            @PathVariable Long scheduleId
    ) {
        return scheduleSeatService
                .initializeScheduleSeats(scheduleId);
    }

    @GetMapping
    public List<ScheduleSeatResponseDto> getScheduleSeats(
            @PathVariable Long scheduleId
    ) {
        return scheduleSeatService
                .getScheduleSeats(scheduleId);
    }
}