package com.movieplatform.backend.controller;

import com.movieplatform.backend.client.TmdbClient;
import com.movieplatform.backend.dto.tmdb.TmdbMovieResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tmdb")
public class TmdbController {

    private final TmdbClient tmdbClient;

    public TmdbController(TmdbClient tmdbClient) {
        this.tmdbClient = tmdbClient;
    }

    @GetMapping("/popular")
    public TmdbMovieResponse getPopularMovies() {
        return tmdbClient.getPopularMovies();
    }
}