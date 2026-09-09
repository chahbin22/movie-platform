package com.movieplatform.backend.service;

import com.movieplatform.backend.client.TmdbClient;
import com.movieplatform.backend.dto.tmdb.TmdbMovieDto;
import com.movieplatform.backend.dto.tmdb.TmdbMovieResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Service
public class TmdbMovieSyncService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    TmdbMovieSyncService.class
            );

    private static final int SYNC_PAGES = 1;

    private final TmdbClient tmdbClient;
    private final MovieService movieService;

    public TmdbMovieSyncService(
            TmdbClient tmdbClient,
            MovieService movieService
    ) {
        this.tmdbClient =
                tmdbClient;

        this.movieService =
                movieService;
    }

    public SyncResult syncMovies() {

        Set<Long> movieIds =
                new LinkedHashSet<>();

        for (
                int page = 1;
                page <= SYNC_PAGES;
                page++
        ) {

            TmdbMovieResponse popular =
                    tmdbClient
                            .getPopularMovies(
                                    page
                            );

            addMovieIds(
                    movieIds,
                    popular
            );

            TmdbMovieResponse nowPlaying =
                    tmdbClient
                            .getNowPlayingMovies(
                                    page
                            );

            addMovieIds(
                    movieIds,
                    nowPlaying
            );
        }

        int inserted = 0;
        int updated = 0;
        int failed = 0;

        for (Long movieId : movieIds) {

            try {

                boolean isInserted =
                        movieService
                                .syncMovieFromTmdb(
                                        movieId
                                );

                if (isInserted) {
                    inserted++;
                } else {
                    updated++;
                }

            } catch (Exception e) {

                failed++;

                log.warn(
                        "TMDB movie sync failed. tmdbMovieId={}",
                        movieId,
                        e
                );
            }
        }

        return new SyncResult(
                movieIds.size(),
                inserted,
                updated,
                failed
        );
    }

    private void addMovieIds(
            Set<Long> movieIds,
            TmdbMovieResponse response
    ) {

        if (
                response == null
                        || response.results()
                        == null
        ) {
            return;
        }

        response.results()
                .stream()
                .filter(Objects::nonNull)
                .map(TmdbMovieDto::id)
                .filter(Objects::nonNull)
                .forEach(movieIds::add);
    }

    public record SyncResult(
            int requested,
            int inserted,
            int updated,
            int failed
    ) {
    }
}