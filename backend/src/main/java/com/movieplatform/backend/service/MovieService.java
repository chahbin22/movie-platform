package com.movieplatform.backend.service;

import com.movieplatform.backend.client.TmdbClient;
import com.movieplatform.backend.dto.movie.MovieResponseDto;
import com.movieplatform.backend.dto.tmdb.TmdbMovieDetailDto;
import com.movieplatform.backend.entity.Movie;
import com.movieplatform.backend.exception.ConflictException;
import com.movieplatform.backend.exception.NotFoundException;
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

    @Transactional
    public MovieResponseDto saveMovieFromTmdb(
            Long tmdbMovieId
    ) {

        if (movieRepository.existsByTmdbMovieId(tmdbMovieId)) {
            throw new ConflictException(
                    "이미 저장된 영화입니다."
            );
        }

        TmdbMovieDetailDto detail =
                tmdbClient.getMovieDetail(tmdbMovieId);

        String genre = null;

        if (detail.genres() != null) {
            genre = detail.genres()
                    .stream()
                    .map(genreDto -> genreDto.name())
                    .collect(Collectors.joining(", "));
        }

        LocalDate releaseDate = null;

        if (detail.releaseDate() != null
                && !detail.releaseDate().isBlank()) {

            releaseDate =
                    LocalDate.parse(detail.releaseDate());
        }

        String posterUrl = null;

        if (detail.posterPath() != null
                && !detail.posterPath().isBlank()) {

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

        Movie savedMovie =
                movieRepository.save(movie);

        return MovieResponseDto.from(savedMovie);
    }

    @Transactional(readOnly = true)
    public List<MovieResponseDto> getMovies() {

        return movieRepository.findAll()
                .stream()
                .map(MovieResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieResponseDto getMovie(
            Long movieId
    ) {

        Movie movie =
                movieRepository.findById(movieId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "영화를 찾을 수 없습니다."
                                )
                        );

        return MovieResponseDto.from(movie);
    }

    @Transactional(readOnly = true)
    public List<MovieResponseDto> searchMovies(
            String keyword
    ) {

        return movieRepository
                .findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(MovieResponseDto::from)
                .toList();
    }
}