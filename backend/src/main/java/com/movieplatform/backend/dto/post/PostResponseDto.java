package com.movieplatform.backend.dto.post;

import com.movieplatform.backend.entity.Post;

import java.time.LocalDateTime;

public record PostResponseDto(
        Long postId,
        Long userId,
        String nickname,
        String title,
        String content,
        Integer viewCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static PostResponseDto from(Post post) {
        return new PostResponseDto(
                post.getPostId(),
                post.getUser().getUserId(),
                post.getUser().getNickname(),
                post.getTitle(),
                post.getContent(),
                post.getViewCount(),
                post.getCreatedAt(),
                post.getUpdatedAt()
        );
    }
}