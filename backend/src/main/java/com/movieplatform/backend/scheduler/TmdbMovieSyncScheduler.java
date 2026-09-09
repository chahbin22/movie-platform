package com.movieplatform.backend.scheduler;

import com.movieplatform.backend.service.TmdbMovieSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TmdbMovieSyncScheduler {

    private static final Logger log =
            LoggerFactory.getLogger(
                    TmdbMovieSyncScheduler.class
            );

    private final TmdbMovieSyncService
            tmdbMovieSyncService;

    public TmdbMovieSyncScheduler(
            TmdbMovieSyncService
                    tmdbMovieSyncService
    ) {
        this.tmdbMovieSyncService =
                tmdbMovieSyncService;
    }

    @EventListener(
            ApplicationReadyEvent.class
    )
    public void syncOnStartup() {

        executeSync("startup");
    }

    @Scheduled(
            cron = "0 0 3 * * *",
            zone = "Asia/Seoul"
    )
    public void syncDaily() {

        executeSync("scheduled");
    }

    private void executeSync(
            String source
    ) {

        try {

            TmdbMovieSyncService.SyncResult
                    result =
                    tmdbMovieSyncService
                            .syncMovies();

            log.info(
                    "TMDB movie sync completed. source={}, requested={}, inserted={}, updated={}, failed={}",
                    source,
                    result.requested(),
                    result.inserted(),
                    result.updated(),
                    result.failed()
            );

        } catch (Exception e) {

            log.error(
                    "TMDB movie sync failed. source={}",
                    source,
                    e
            );
        }
    }
}