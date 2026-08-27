package com.movieplatform.backend.dto.schedule;

import com.movieplatform.backend.entity.Schedule;

import java.time.LocalDateTime;

public record ScheduleResponseDto(
        Long scheduleId,

        Long movieId,
        String movieTitle,

        Long theaterId,
        String theaterName,

        Long screenId,
        String screenName,

        LocalDateTime startTime,
        Integer basePrice
) {

    public static ScheduleResponseDto from(Schedule schedule) {
        return new ScheduleResponseDto(
                schedule.getScheduleId(),

                schedule.getMovie().getMovieId(),
                schedule.getMovie().getTitle(),

                schedule.getScreen()
                        .getTheater()
                        .getTheaterId(),

                schedule.getScreen()
                        .getTheater()
                        .getName(),

                schedule.getScreen().getScreenId(),
                schedule.getScreen().getName(),

                schedule.getStartTime(),
                schedule.getBasePrice()
        );
    }
}