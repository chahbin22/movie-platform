package com.movieplatform.backend.dto.tmdb;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TmdbMovieDto(
        Long id,
        String title,
        String overview,

        @JsonProperty("release_date")
        String releaseDate,

        @JsonProperty("poster_path")
        String posterPath
) {
}