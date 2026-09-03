package com.movieplatform.backend.repository;

import com.movieplatform.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository
        extends JpaRepository<Post, Long> {

    List<Post> findAllByOrderByCreatedAtDesc();
}