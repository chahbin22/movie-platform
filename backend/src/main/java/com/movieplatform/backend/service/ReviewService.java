package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.review.ReviewRequest;
import com.movieplatform.backend.dto.review.ReviewResponseDto;
import com.movieplatform.backend.entity.Movie;
import com.movieplatform.backend.entity.Review;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.repository.MovieRepository;
import com.movieplatform.backend.repository.ReviewRepository;
import com.movieplatform.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            MovieRepository movieRepository,
            UserRepository userRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReviewResponseDto createReview(
            Long userId,
            Long movieId,
            ReviewRequest request
    ) {

        if (reviewRepository
                .existsByUser_UserIdAndMovie_MovieId(
                        userId,
                        movieId
                )) {

            throw new IllegalArgumentException(
                    "이미 이 영화에 리뷰를 작성했습니다."
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다."
                        )
                );

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "영화를 찾을 수 없습니다."
                        )
                );

        Review review = new Review(
                user,
                movie,
                request.rating(),
                request.content()
        );

        Review savedReview =
                reviewRepository.save(review);

        return ReviewResponseDto.from(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getMovieReviews(
            Long movieId
    ) {

        if (!movieRepository.existsById(movieId)) {
            throw new IllegalArgumentException(
                    "영화를 찾을 수 없습니다."
            );
        }

        return reviewRepository
                .findByMovie_MovieIdOrderByCreatedAtDesc(movieId)
                .stream()
                .map(ReviewResponseDto::from)
                .toList();
    }
}