package com.movieplatform.backend.service;

import com.movieplatform.backend.client.TmdbClient;
import com.movieplatform.backend.dto.movie.MovieResponseDto;
import com.movieplatform.backend.entity.Movie;
import com.movieplatform.backend.exception.ConflictException;
import com.movieplatform.backend.exception.NotFoundException;
import com.movieplatform.backend.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private TmdbClient tmdbClient;

    private MovieService movieService;

    @BeforeEach
    void setUp() {
        movieService = new MovieService(
                movieRepository,
                tmdbClient
        );
    }

    @Test
    void 영화가_존재하지_않으면_NotFoundException() {

        Long movieId = 999L;

        when(movieRepository.findById(movieId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> movieService.getMovie(movieId)
        );

        assertEquals(
                "영화를 찾을 수 없습니다.",
                exception.getMessage()
        );

        verify(movieRepository).findById(movieId);
    }

    @Test
    void 이미_저장된_TMDB_영화면_ConflictException() {

        Long tmdbMovieId = 123L;

        when(movieRepository.existsByTmdbMovieId(tmdbMovieId))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> movieService.saveMovieFromTmdb(tmdbMovieId)
        );

        assertEquals(
                "이미 저장된 영화입니다.",
                exception.getMessage()
        );

        verify(movieRepository)
                .existsByTmdbMovieId(tmdbMovieId);

        verifyNoInteractions(tmdbClient);

        verify(movieRepository, never())
                .save(any(Movie.class));
    }

    @Test
    void 저장된_영화_목록을_조회할_수_있다() {

        Movie movie1 = new Movie(
                550L,
                "Fight Club",
                "영화 설명 1",
                "David Fincher",
                "Drama",
                139,
                LocalDate.of(1999, 10, 15),
                "poster1.jpg",
                "청소년 관람불가"
        );

        Movie movie2 = new Movie(
                680L,
                "Pulp Fiction",
                "영화 설명 2",
                "Quentin Tarantino",
                "Crime",
                154,
                LocalDate.of(1994, 10, 14),
                "poster2.jpg",
                "청소년 관람불가"
        );

        when(movieRepository.findAll())
                .thenReturn(List.of(movie1, movie2));

        List<MovieResponseDto> result =
                movieService.getMovies();

        assertEquals(2, result.size());

        verify(movieRepository).findAll();
    }
}