package com.movieplatform.backend.dto.review;

import com.movieplatform.backend.entity.Review;

import java.time.LocalDateTime;

public record ReviewResponseDto(
        Long reviewId,
        Long userId,
        String nickname,
        Long movieId,
        Integer rating,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ReviewResponseDto from(Review review) {
        return new ReviewResponseDto(
                review.getReviewId(),
                review.getUser().getUserId(),
                review.getUser().getNickname(),
                review.getMovie().getMovieId(),
                review.getRating(),
                review.getContent(),
                review.getCreatedAt(),
                review.getUpdatedAt()
        );
    }
}