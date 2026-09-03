package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository
        extends JpaRepository<Comment, Long> {

    List<Comment>
    findByPost_PostIdOrderByCreatedAtAsc(
            Long postId
    );
}