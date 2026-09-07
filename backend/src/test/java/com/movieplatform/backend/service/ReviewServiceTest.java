package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.review.ReviewRequest;
import com.movieplatform.backend.entity.Movie;
import com.movieplatform.backend.entity.Review;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.exception.ConflictException;
import com.movieplatform.backend.exception.ForbiddenException;
import com.movieplatform.backend.exception.NotFoundException;
import com.movieplatform.backend.repository.MovieRepository;
import com.movieplatform.backend.repository.ReviewRepository;
import com.movieplatform.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private UserRepository userRepository;

    private ReviewService reviewService;

    @BeforeEach
    void setUp() {
        reviewService = new ReviewService(
                reviewRepository,
                movieRepository,
                userRepository
        );
    }

    @Test
    void 이미_리뷰를_작성했으면_ConflictException() {

        Long userId = 1L;
        Long movieId = 1L;

        ReviewRequest request =
                new ReviewRequest(5, "재미있는 영화");

        when(reviewRepository
                .existsByUser_UserIdAndMovie_MovieId(
                        userId,
                        movieId
                ))
                .thenReturn(true);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> reviewService.createReview(
                        userId,
                        movieId,
                        request
                )
        );

        assertEquals(
                "이미 이 영화에 리뷰를 작성했습니다.",
                exception.getMessage()
        );

        verify(reviewRepository)
                .existsByUser_UserIdAndMovie_MovieId(
                        userId,
                        movieId
                );

        verifyNoInteractions(
                userRepository,
                movieRepository
        );
    }

    @Test
    void 영화가_존재하지_않으면_NotFoundException() {

        Long userId = 1L;
        Long movieId = 999L;

        ReviewRequest request =
                new ReviewRequest(5, "재미있는 영화");

        User user = mock(User.class);

        when(reviewRepository
                .existsByUser_UserIdAndMovie_MovieId(
                        userId,
                        movieId
                ))
                .thenReturn(false);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(movieRepository.findById(movieId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> reviewService.createReview(
                        userId,
                        movieId,
                        request
                )
        );

        assertEquals(
                "영화를 찾을 수 없습니다.",
                exception.getMessage()
        );

        verify(movieRepository).findById(movieId);
        verify(reviewRepository, never())
                .save(any(Review.class));
    }

    @Test
    void 남의_리뷰는_수정할_수_없다() {

        Long loginUserId = 1L;
        Long reviewOwnerId = 2L;
        Long movieId = 10L;
        Long reviewId = 100L;

        ReviewRequest request =
                new ReviewRequest(4, "수정된 리뷰");

        User reviewOwner = mock(User.class);
        Movie movie = mock(Movie.class);
        Review review = mock(Review.class);

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(review));

        when(review.getMovie())
                .thenReturn(movie);

        when(movie.getMovieId())
                .thenReturn(movieId);

        when(review.getUser())
                .thenReturn(reviewOwner);

        when(reviewOwner.getUserId())
                .thenReturn(reviewOwnerId);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> reviewService.updateReview(
                        loginUserId,
                        movieId,
                        reviewId,
                        request
                )
        );

        assertEquals(
                "본인의 리뷰만 수정할 수 있습니다.",
                exception.getMessage()
        );

        verify(review, never())
                .update(anyInt(), anyString());
    }

    @Test
    void 남의_리뷰는_삭제할_수_없다() {

        Long loginUserId = 1L;
        Long reviewOwnerId = 2L;
        Long movieId = 10L;
        Long reviewId = 100L;

        User reviewOwner = mock(User.class);
        Movie movie = mock(Movie.class);
        Review review = mock(Review.class);

        when(reviewRepository.findById(reviewId))
                .thenReturn(Optional.of(review));

        when(review.getMovie())
                .thenReturn(movie);

        when(movie.getMovieId())
                .thenReturn(movieId);

        when(review.getUser())
                .thenReturn(reviewOwner);

        when(reviewOwner.getUserId())
                .thenReturn(reviewOwnerId);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> reviewService.deleteReview(
                        loginUserId,
                        movieId,
                        reviewId
                )
        );

        assertEquals(
                "본인의 리뷰만 삭제할 수 있습니다.",
                exception.getMessage()
        );

        verify(reviewRepository, never())
                .delete(review);
    }
}