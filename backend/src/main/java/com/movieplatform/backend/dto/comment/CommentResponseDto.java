package com.movieplatform.backend.dto.comment;

import com.movieplatform.backend.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponseDto(
        Long commentId,
        Long postId,
        Long userId,
        String nickname,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CommentResponseDto from(Comment comment) {
        return new CommentResponseDto(
                comment.getCommentId(),
                comment.getPost().getPostId(),
                comment.getUser().getUserId(),
                comment.getUser().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}