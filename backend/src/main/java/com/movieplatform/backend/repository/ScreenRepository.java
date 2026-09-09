package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScreenRepository
        extends JpaRepository<Screen, Long> {

    List<Screen> findByTheater_TheaterId(
            Long theaterId
    );

    Optional<Screen>
    findFirstByTheater_TheaterIdAndName(
            Long theaterId,
            String name
    );
}