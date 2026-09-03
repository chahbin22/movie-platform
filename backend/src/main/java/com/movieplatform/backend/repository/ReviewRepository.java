package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    boolean existsByUser_UserIdAndMovie_MovieId(
            Long userId,
            Long movieId
    );

    List<Review>
    findByMovie_MovieIdOrderByCreatedAtDesc(
            Long movieId
    );
}