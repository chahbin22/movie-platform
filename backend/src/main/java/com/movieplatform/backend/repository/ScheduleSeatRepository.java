package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleSeatRepository
        extends JpaRepository<ScheduleSeat, Long> {

    List<ScheduleSeat>
    findBySchedule_ScheduleIdOrderBySeat_SeatRowAscSeat_SeatNumberAsc(
            Long scheduleId
    );

    boolean existsBySchedule_ScheduleId(Long scheduleId);
}