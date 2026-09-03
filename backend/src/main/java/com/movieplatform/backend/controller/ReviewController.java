package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.review.ReviewRequest;
import com.movieplatform.backend.dto.review.ReviewResponseDto;
import com.movieplatform.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies/{movieId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService
    ) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ReviewResponseDto createReview(
            Authentication authentication,
            @PathVariable Long movieId,
            @Valid @RequestBody ReviewRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return reviewService.createReview(
                userId,
                movieId,
                request
        );
    }

    @GetMapping
    public List<ReviewResponseDto> getMovieReviews(
            @PathVariable Long movieId
    ) {

        return reviewService.getMovieReviews(movieId);
    }
}