package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.comment.CommentRequest;
import com.movieplatform.backend.dto.comment.CommentResponseDto;
import com.movieplatform.backend.entity.Comment;
import com.movieplatform.backend.entity.Post;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.repository.CommentRepository;
import com.movieplatform.backend.repository.PostRepository;
import com.movieplatform.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository,
            UserRepository userRepository
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CommentResponseDto createComment(
            Long userId,
            Long postId,
            CommentRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다."
                        )
                );

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "게시글을 찾을 수 없습니다."
                        )
                );

        Comment comment = new Comment(
                user,
                post,
                request.content()
        );

        Comment savedComment =
                commentRepository.save(comment);

        return CommentResponseDto.from(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getComments(
            Long postId
    ) {

        if (!postRepository.existsById(postId)) {
            throw new IllegalArgumentException(
                    "게시글을 찾을 수 없습니다."
            );
        }

        return commentRepository
                .findByPost_PostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(CommentResponseDto::from)
                .toList();
    }

    @Transactional
    public CommentResponseDto updateComment(
            Long userId,
            Long postId,
            Long commentId,
            CommentRequest request
    ) {

        Comment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "댓글을 찾을 수 없습니다."
                                )
                        );

        if (!comment.getPost()
                .getPostId()
                .equals(postId)) {

            throw new IllegalArgumentException(
                    "해당 게시글의 댓글이 아닙니다."
            );
        }

        if (!comment.getUser()
                .getUserId()
                .equals(userId)) {

            throw new IllegalArgumentException(
                    "본인의 댓글만 수정할 수 있습니다."
            );
        }

        comment.update(request.content());

        return CommentResponseDto.from(comment);
    }

    @Transactional
    public void deleteComment(
            Long userId,
            Long postId,
            Long commentId
    ) {

        Comment comment =
                commentRepository.findById(commentId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "댓글을 찾을 수 없습니다."
                                )
                        );

        if (!comment.getPost()
                .getPostId()
                .equals(postId)) {

            throw new IllegalArgumentException(
                    "해당 게시글의 댓글이 아닙니다."
            );
        }

        if (!comment.getUser()
                .getUserId()
                .equals(userId)) {

            throw new IllegalArgumentException(
                    "본인의 댓글만 삭제할 수 있습니다."
            );
        }

        commentRepository.delete(comment);
    }
}