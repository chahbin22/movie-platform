package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository
        extends JpaRepository<Schedule, Long> {

    List<Schedule>
    findByMovie_MovieIdAndScreen_Theater_TheaterIdAndStartTimeBetweenOrderByStartTimeAsc(
            Long movieId,
            Long theaterId,
            LocalDateTime start,
            LocalDateTime end
    );
}