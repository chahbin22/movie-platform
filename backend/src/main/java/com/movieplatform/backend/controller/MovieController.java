package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.movie.MovieResponseDto;
import com.movieplatform.backend.service.MovieService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public List<MovieResponseDto> getMovies() {
        return movieService.getMovies();
    }

    @GetMapping("/{movieId}")
    public MovieResponseDto getMovie(
            @PathVariable Long movieId
    ) {
        return movieService.getMovie(movieId);
    }
}