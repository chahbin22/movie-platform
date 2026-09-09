package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRepository
        extends JpaRepository<Movie, Long> {

    boolean existsByTmdbMovieId(
            Long tmdbMovieId
    );

    Optional<Movie> findByTmdbMovieId(
            Long tmdbMovieId
    );

    List<Movie> findByTitleContainingIgnoreCase(
            String keyword
    );
}