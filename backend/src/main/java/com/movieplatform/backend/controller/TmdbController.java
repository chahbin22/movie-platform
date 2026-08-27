package com.movieplatform.backend.controller;

import com.movieplatform.backend.client.TmdbClient;
import com.movieplatform.backend.dto.tmdb.TmdbMovieDetailDto;
import com.movieplatform.backend.dto.tmdb.TmdbMovieResponse;
import com.movieplatform.backend.entity.Movie;
import com.movieplatform.backend.service.MovieService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    private final TmdbClient tmdbClient;
    private final MovieService movieService;

    public TmdbController(
            TmdbClient tmdbClient,
            MovieService movieService
    ) {
        this.tmdbClient = tmdbClient;
        this.movieService = movieService;
    }

    @GetMapping("/popular")
    public TmdbMovieResponse getPopularMovies() {
        return tmdbClient.getPopularMovies();
    }

    @GetMapping("/movies/{movieId}")
    public TmdbMovieDetailDto getMovieDetail(
            @PathVariable Long movieId
    ) {
        return tmdbClient.getMovieDetail(movieId);
    }

    @PostMapping("/movies/{movieId}/save")
    public Movie saveMovie(
            @PathVariable Long movieId
    ) {
        return movieService.saveMovieFromTmdb(movieId);
    }
}