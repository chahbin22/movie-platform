package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    boolean existsByTmdbMovieId(Long tmdbMovieId);

    List<Movie> findByTitleContainingIgnoreCase(String keyword);
}