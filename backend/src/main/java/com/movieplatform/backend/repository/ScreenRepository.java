package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScreenRepository
        extends JpaRepository<Screen, Long> {

    List<Screen> findByTheater_TheaterId(Long theaterId);
}