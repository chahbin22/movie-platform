package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.ScheduleSeat;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScheduleSeatRepository
        extends JpaRepository<ScheduleSeat, Long> {

    List<ScheduleSeat>
    findBySchedule_ScheduleIdOrderBySeat_SeatRowAscSeat_SeatNumberAsc(
            Long scheduleId
    );

    boolean existsBySchedule_ScheduleId(Long scheduleId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT ss
            FROM ScheduleSeat ss
            JOIN FETCH ss.seat
            WHERE ss.scheduleSeatId IN :ids
            ORDER BY ss.scheduleSeatId
            """)
    List<ScheduleSeat> findAllByIdsWithLock(
            @Param("ids") List<Long> ids
    );
}