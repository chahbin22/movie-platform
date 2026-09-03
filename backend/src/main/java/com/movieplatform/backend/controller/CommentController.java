package com.movieplatform.backend.controller;

import com.movieplatform.backend.dto.comment.CommentRequest;
import com.movieplatform.backend.dto.comment.CommentResponseDto;
import com.movieplatform.backend.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(
            CommentService commentService
    ) {
        this.commentService = commentService;
    }

    @PostMapping
    public CommentResponseDto createComment(
            Authentication authentication,
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return commentService.createComment(
                userId,
                postId,
                request
        );
    }

    @GetMapping
    public List<CommentResponseDto> getComments(
            @PathVariable Long postId
    ) {

        return commentService.getComments(postId);
    }

    @PatchMapping("/{commentId}")
    public CommentResponseDto updateComment(
            Authentication authentication,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentRequest request
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        return commentService.updateComment(
                userId,
                postId,
                commentId,
                request
        );
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(
            Authentication authentication,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {

        Long userId =
                (Long) authentication.getPrincipal();

        commentService.deleteComment(
                userId,
                postId,
                commentId
        );
    }
}