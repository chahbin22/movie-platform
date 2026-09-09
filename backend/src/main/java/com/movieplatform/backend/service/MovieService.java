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
import java.time.format.DateTimeParseException;
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

        if (
                movieRepository
                        .existsByTmdbMovieId(
                                tmdbMovieId
                        )
        ) {
            throw new ConflictException(
                    "이미 저장된 영화입니다."
            );
        }

        TmdbMovieDetailDto detail =
                getTmdbMovieDetail(
                        tmdbMovieId
                );

        Movie movie =
                createMovieFromDetail(
                        detail
                );

        Movie savedMovie =
                movieRepository.save(
                        movie
                );

        return MovieResponseDto.from(
                savedMovie
        );
    }

    @Transactional
    public boolean syncMovieFromTmdb(
            Long tmdbMovieId
    ) {

        TmdbMovieDetailDto detail =
                getTmdbMovieDetail(
                        tmdbMovieId
                );

        String title =
                getSafeTitle(detail);

        String genre =
                getGenre(detail);

        LocalDate releaseDate =
                getReleaseDate(detail);

        String posterUrl =
                getPosterUrl(detail);

        Integer runningTime =
                getRunningTime(detail);

        return movieRepository
                .findByTmdbMovieId(
                        tmdbMovieId
                )
                .map(movie -> {

                    movie.updateFromTmdb(
                            title,
                            detail.overview(),
                            genre,
                            runningTime,
                            releaseDate,
                            posterUrl
                    );

                    return false;
                })
                .orElseGet(() -> {

                    Movie newMovie =
                            new Movie(
                                    detail.id(),
                                    title,
                                    detail.overview(),
                                    null,
                                    genre,
                                    runningTime,
                                    releaseDate,
                                    posterUrl,
                                    null
                            );

                    movieRepository.save(
                            newMovie
                    );

                    return true;
                });
    }

    @Transactional(readOnly = true)
    public List<MovieResponseDto> getMovies() {

        return movieRepository
                .findAll()
                .stream()
                .map(
                        MovieResponseDto::from
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public MovieResponseDto getMovie(
            Long movieId
    ) {

        Movie movie =
                movieRepository
                        .findById(movieId)
                        .orElseThrow(() ->
                                new NotFoundException(
                                        "영화를 찾을 수 없습니다."
                                )
                        );

        return MovieResponseDto.from(
                movie
        );
    }

    @Transactional(readOnly = true)
    public List<MovieResponseDto> searchMovies(
            String keyword
    ) {

        return movieRepository
                .findByTitleContainingIgnoreCase(
                        keyword
                )
                .stream()
                .map(
                        MovieResponseDto::from
                )
                .toList();
    }

    private TmdbMovieDetailDto getTmdbMovieDetail(
            Long tmdbMovieId
    ) {

        TmdbMovieDetailDto detail =
                tmdbClient.getMovieDetail(
                        tmdbMovieId
                );

        if (detail == null) {
            throw new NotFoundException(
                    "TMDB 영화 정보를 불러올 수 없습니다."
            );
        }

        return detail;
    }

    private Movie createMovieFromDetail(
            TmdbMovieDetailDto detail
    ) {

        return new Movie(
                detail.id(),
                getSafeTitle(detail),
                detail.overview(),
                null,
                getGenre(detail),
                getRunningTime(detail),
                getReleaseDate(detail),
                getPosterUrl(detail),
                null
        );
    }

    private String getSafeTitle(
            TmdbMovieDetailDto detail
    ) {

        if (
                detail.title() == null
                        || detail.title().isBlank()
        ) {
            return "제목 없음";
        }

        return detail.title();
    }

    private String getGenre(
            TmdbMovieDetailDto detail
    ) {

        if (
                detail.genres() == null
                        || detail.genres().isEmpty()
        ) {
            return null;
        }

        return detail.genres()
                .stream()
                .map(
                        genreDto ->
                                genreDto.name()
                )
                .filter(
                        name ->
                                name != null
                                        && !name.isBlank()
                )
                .collect(
                        Collectors.joining(", ")
                );
    }

    private LocalDate getReleaseDate(
            TmdbMovieDetailDto detail
    ) {

        if (
                detail.releaseDate() == null
                        || detail.releaseDate().isBlank()
        ) {
            return null;
        }

        try {
            return LocalDate.parse(
                    detail.releaseDate()
            );
        } catch (
                DateTimeParseException e
        ) {
            return null;
        }
    }

    private String getPosterUrl(
            TmdbMovieDetailDto detail
    ) {

        if (
                detail.posterPath() == null
                        || detail.posterPath().isBlank()
        ) {
            return null;
        }

        return "https://image.tmdb.org/t/p/w500"
                + detail.posterPath();
    }

    private Integer getRunningTime(
            TmdbMovieDetailDto detail
    ) {

        if (detail.runtime() == null) {
            return 0;
        }

        return detail.runtime();
    }
}