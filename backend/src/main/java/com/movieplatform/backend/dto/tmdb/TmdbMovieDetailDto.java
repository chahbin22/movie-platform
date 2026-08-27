package com.movieplatform.backend.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record TmdbMovieDetailDto(
        Long id,
        String title,
        String overview,

        @JsonProperty("release_date")
        String releaseDate,

        @JsonProperty("poster_path")
        String posterPath,

        Integer runtime,

        List<TmdbGenreDto> genres
) {
}