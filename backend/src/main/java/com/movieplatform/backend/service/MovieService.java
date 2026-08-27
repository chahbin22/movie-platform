package com.movieplatform.backend.service;

import com.movieplatform.backend.client.TmdbClient;
import com.movieplatform.backend.dto.tmdb.TmdbMovieDetailDto;
import com.movieplatform.backend.entity.Movie;
import com.movieplatform.backend.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;
    private final TmdbClient tmdbClient;

    public MovieService(
            MovieRepository movieRepository,
            TmdbClient tmdbClient
    ) {
        this.movieRepository = movieRepository;
        this.tmdbClient = tmdbClient;
    }

    @Transactional(readOnly = true)
    public List<Movie> getMovies() {
        return movieRepository.findAll();
    }

    @Transactional
    public Movie saveMovieFromTmdb(Long tmdbMovieId) {

        if (movieRepository.existsByTmdbMovieId(tmdbMovieId)) {
            throw new IllegalArgumentException("이미 저장된 영화입니다.");
        }

        TmdbMovieDetailDto detail =
                tmdbClient.getMovieDetail(tmdbMovieId);

        String genre = detail.genres().stream()
                .map(g -> g.name())
                .collect(Collectors.joining(", "));

        LocalDate releaseDate = null;

        if (detail.releaseDate() != null
                && !detail.releaseDate().isBlank()) {
            releaseDate = LocalDate.parse(detail.releaseDate());
        }

        String posterUrl = null;

        if (detail.posterPath() != null) {
            posterUrl =
                    "https://image.tmdb.org/t/p/w500"
                    + detail.posterPath();
        }

        Movie movie = new Movie(
                detail.id(),
                detail.title(),
                detail.overview(),
                null,
                genre,
                detail.runtime(),
                releaseDate,
                posterUrl,
                null
        );

        return movieRepository.save(movie);
    }
}