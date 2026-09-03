package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.post.PostRequest;
import com.movieplatform.backend.dto.post.PostResponseDto;
import com.movieplatform.backend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(
            PostService postService
    ) {
        this.postService = postService;
    }

    @PostMapping
    public PostResponseDto createPost(
            Authentication authentication,
            @Valid @RequestBody PostRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return postService.createPost(
                userId,
                request
        );
    }

    @GetMapping
    public List<PostResponseDto> getPosts() {
        return postService.getPosts();
    }

    @GetMapping("/{postId}")
    public PostResponseDto getPost(
            @PathVariable Long postId
    ) {
        return postService.getPost(postId);
    }

    @PatchMapping("/{postId}")
    public PostResponseDto updatePost(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody PostRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return postService.updatePost(
                userId,
                postId,
                request
        );
    }

    @DeleteMapping("/{postId}")
    public void deletePost(
            Authentication authentication,
            @PathVariable Long postId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        postService.deletePost(
                userId,
                postId
        );
    }
}