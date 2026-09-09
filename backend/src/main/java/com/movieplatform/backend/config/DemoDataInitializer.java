package com.movieplatform.backend.config;

import com.movieplatform.backend.entity.Movie;
import com.movieplatform.backend.entity.Schedule;
import com.movieplatform.backend.entity.Screen;
import com.movieplatform.backend.entity.Seat;
import com.movieplatform.backend.entity.Theater;
import com.movieplatform.backend.repository.MovieRepository;
import com.movieplatform.backend.repository.ScheduleRepository;
import com.movieplatform.backend.repository.ScreenRepository;
import com.movieplatform.backend.repository.SeatRepository;
import com.movieplatform.backend.repository.TheaterRepository;
import com.movieplatform.backend.service.ScheduleSeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Component
public class DemoDataInitializer {

    private static final Logger log =
            LoggerFactory.getLogger(
                    DemoDataInitializer.class
            );

    private static final String DEMO_BRAND =
            "MOVIE PLATFORM";

    private static final int MAX_DEMO_MOVIES =
            10;

    private static final int DEMO_DAYS =
            4;

    private static final List<String>
            SCREEN_NAMES =
            List.of(
                    "1관",
                    "2관"
            );

    private static final List<LocalTime>
            SHOW_TIMES =
            List.of(
                    LocalTime.of(10, 0),
                    LocalTime.of(12, 30),
                    LocalTime.of(15, 0),
                    LocalTime.of(17, 30),
                    LocalTime.of(20, 0)
            );

    private static final List<TheaterSeed>
            THEATER_SEEDS =
            List.of(
                    new TheaterSeed(
                            "강남점",
                            "서울특별시 강남구 (데모)"
                    ),
                    new TheaterSeed(
                            "건대점",
                            "서울특별시 광진구 (데모)"
                    ),
                    new TheaterSeed(
                            "홍대점",
                            "서울특별시 마포구 (데모)"
                    ),
                    new TheaterSeed(
                            "신촌점",
                            "서울특별시 서대문구 (데모)"
                    ),
                    new TheaterSeed(
                            "잠실점",
                            "서울특별시 송파구 (데모)"
                    )
            );

    private final TheaterRepository
            theaterRepository;

    private final ScreenRepository
            screenRepository;

    private final SeatRepository
            seatRepository;

    private final MovieRepository
            movieRepository;

    private final ScheduleRepository
            scheduleRepository;

    private final ScheduleSeatService
            scheduleSeatService;

    public DemoDataInitializer(
            TheaterRepository theaterRepository,
            ScreenRepository screenRepository,
            SeatRepository seatRepository,
            MovieRepository movieRepository,
            ScheduleRepository scheduleRepository,
            ScheduleSeatService scheduleSeatService
    ) {
        this.theaterRepository =
                theaterRepository;

        this.screenRepository =
                screenRepository;

        this.seatRepository =
                seatRepository;

        this.movieRepository =
                movieRepository;

        this.scheduleRepository =
                scheduleRepository;

        this.scheduleSeatService =
                scheduleSeatService;
    }

    @Order(2)
    @EventListener(
            ApplicationReadyEvent.class
    )
    public void initializeDemoData() {

        log.info(
                "Demo data initialization started."
        );

        List<Movie> movies =
                movieRepository
                        .findAll(
                                Sort.by(
                                        Sort.Direction.ASC,
                                        "movieId"
                                )
                        )
                        .stream()
                        .limit(MAX_DEMO_MOVIES)
                        .toList();

        if (movies.isEmpty()) {

            log.warn(
                    "Demo data initialization skipped because no movies exist."
            );

            return;
        }

        List<Theater> theaters =
                initializeTheaters();

        for (Theater theater : theaters) {

            List<Screen> screens =
                    initializeScreens(
                            theater
                    );

            for (Screen screen : screens) {

                initializeSeats(
                        screen
                );
            }

            initializeSchedules(
                    theater,
                    screens,
                    movies
            );
        }

        log.info(
                "Demo data initialization completed. theaters={}, movies={}",
                theaters.size(),
                movies.size()
        );
    }

    private List<Theater> initializeTheaters() {

        List<Theater> theaters =
                new ArrayList<>();

        for (
                TheaterSeed seed
                : THEATER_SEEDS
        ) {

            Theater theater =
                    theaterRepository
                            .findFirstByBrandAndName(
                                    DEMO_BRAND,
                                    seed.name()
                            )
                            .orElseGet(() ->
                                    theaterRepository.save(
                                            new Theater(
                                                    DEMO_BRAND,
                                                    seed.name(),
                                                    seed.address(),
                                                    null,
                                                    null
                                            )
                                    )
                            );

            theaters.add(
                    theater
            );
        }

        return theaters;
    }

    private List<Screen> initializeScreens(
            Theater theater
    ) {

        List<Screen> screens =
                new ArrayList<>();

        for (
                String screenName
                : SCREEN_NAMES
        ) {

            Screen screen =
                    screenRepository
                            .findFirstByTheater_TheaterIdAndName(
                                    theater.getTheaterId(),
                                    screenName
                            )
                            .orElseGet(() ->
                                    screenRepository.save(
                                            new Screen(
                                                    screenName,
                                                    theater
                                            )
                                    )
                            );

            screens.add(
                    screen
            );
        }

        return screens;
    }

    private void initializeSeats(
            Screen screen
    ) {

        List<Seat> newSeats =
                new ArrayList<>();

        for (
                char row = 'A';
                row <= 'E';
                row++
        ) {

            String seatRow =
                    String.valueOf(row);

            for (
                    int seatNumber = 1;
                    seatNumber <= 6;
                    seatNumber++
            ) {

                boolean exists =
                        seatRepository
                                .existsByScreen_ScreenIdAndSeatRowAndSeatNumber(
                                        screen.getScreenId(),
                                        seatRow,
                                        seatNumber
                                );

                if (!exists) {

                    newSeats.add(
                            new Seat(
                                    seatRow,
                                    seatNumber,
                                    "NORMAL",
                                    screen
                            )
                    );
                }
            }
        }

        if (!newSeats.isEmpty()) {

            seatRepository.saveAll(
                    newSeats
            );
        }
    }

    private void initializeSchedules(
            Theater theater,
            List<Screen> screens,
            List<Movie> movies
    ) {

        LocalDate today =
                LocalDate.now(
                        ZoneId.of(
                                "Asia/Seoul"
                        )
                );

        for (
                int dayOffset = 0;
                dayOffset < DEMO_DAYS;
                dayOffset++
        ) {

            LocalDate date =
                    today.plusDays(
                            dayOffset
                    );

            for (
                    int movieIndex = 0;
                    movieIndex < movies.size();
                    movieIndex++
            ) {

                Movie movie =
                        movies.get(
                                movieIndex
                        );

                int screenIndex =
                        movieIndex
                                % screens.size();

                int timeIndex =
                        movieIndex
                                / screens.size();

                Screen screen =
                        screens.get(
                                screenIndex
                        );

                LocalTime showTime =
                        SHOW_TIMES.get(
                                timeIndex
                        );

                LocalDateTime startTime =
                        date.atTime(
                                showTime
                        );

                int basePrice =
                        getBasePrice(
                                date
                        );

                Schedule schedule =
                        scheduleRepository
                                .findFirstByScreen_ScreenIdAndStartTime(
                                        screen.getScreenId(),
                                        startTime
                                )
                                .orElseGet(() ->
                                        scheduleRepository.save(
                                                new Schedule(
                                                        startTime,
                                                        basePrice,
                                                        movie,
                                                        screen
                                                )
                                        )
                                );

                scheduleSeatService
                        .initializeScheduleSeats(
                                schedule.getScheduleId()
                        );
            }
        }
    }

    private int getBasePrice(
            LocalDate date
    ) {

        DayOfWeek dayOfWeek =
                date.getDayOfWeek();

        if (
                dayOfWeek
                        == DayOfWeek.SATURDAY
                        || dayOfWeek
                        == DayOfWeek.SUNDAY
        ) {
            return 16000;
        }

        return 15000;
    }

    private record TheaterSeed(
            String name,
            String address
    ) {
    }
}