package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation>
    findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}