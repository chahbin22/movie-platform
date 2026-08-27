package com.movieplatform.backend.dto.movie;

import com.movieplatform.backend.entity.Movie;

import java.time.LocalDate;

public record MovieResponseDto(
        Long movieId,
        Long tmdbMovieId,
        String title,
        String description,
        String director,
        String genre,
        Integer runningTime,
        LocalDate releaseDate,
        String posterUrl,
        String ageRating
) {

    public static MovieResponseDto from(Movie movie) {
        return new MovieResponseDto(
                movie.getMovieId(),
                movie.getTmdbMovieId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getDirector(),
                movie.getGenre(),
                movie.getRunningTime(),
                movie.getReleaseDate(),
                movie.getPosterUrl(),
                movie.getAgeRating()
        );
    }
}