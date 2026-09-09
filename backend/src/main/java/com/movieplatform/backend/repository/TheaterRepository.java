package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Theater;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TheaterRepository
        extends JpaRepository<Theater, Long> {

    Optional<Theater> findFirstByBrandAndName(
            String brand,
            String name
    );
}