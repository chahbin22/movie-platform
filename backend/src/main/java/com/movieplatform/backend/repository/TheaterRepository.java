package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TheaterRepository
        extends JpaRepository<Theater, Long> {
}