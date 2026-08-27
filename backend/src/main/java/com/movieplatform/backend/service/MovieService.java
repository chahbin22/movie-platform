package com.movieplatform.backend.service;

import com.movieplatform.backend.entity.Movie;
import com.movieplatform.backend.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Transactional(readOnly = true)
    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }
}