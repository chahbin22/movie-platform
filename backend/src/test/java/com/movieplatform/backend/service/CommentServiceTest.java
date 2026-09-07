package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.comment.CommentRequest;
import com.movieplatform.backend.dto.comment.CommentResponseDto;
import com.movieplatform.backend.entity.Comment;
import com.movieplatform.backend.entity.Post;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.exception.ForbiddenException;
import com.movieplatform.backend.exception.NotFoundException;
import com.movieplatform.backend.repository.CommentRepository;
import com.movieplatform.backend.repository.PostRepository;
import com.movieplatform.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentService(
                commentRepository,
                postRepository,
                userRepository
        );
    }

    @Test
    void 없는_게시글의_댓글을_조회하면_NotFoundException() {

        Long postId = 999L;

        when(postRepository.existsById(postId))
                .thenReturn(false);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> commentService.getComments(postId)
        );

        assertEquals(
                "게시글을 찾을 수 없습니다.",
                exception.getMessage()
        );

        verifyNoInteractions(commentRepository);
    }

    @Test
    void 댓글이_없으면_빈_목록을_반환한다() {

        Long postId = 1L;

        when(postRepository.existsById(postId))
                .thenReturn(true);

        when(commentRepository
                .findByPost_PostIdOrderByCreatedAtAsc(postId))
                .thenReturn(Collections.emptyList());

        List<CommentResponseDto> result =
                commentService.getComments(postId);

        assertTrue(result.isEmpty());

        verify(commentRepository)
                .findByPost_PostIdOrderByCreatedAtAsc(postId);
    }

    @Test
    void 다른_게시글의_댓글은_수정할_수_없다() {

        Long loginUserId = 1L;
        Long requestedPostId = 10L;
        Long actualPostId = 20L;
        Long commentId = 100L;

        CommentRequest request =
                new CommentRequest("수정 내용");

        Comment comment = mock(Comment.class);
        Post post = mock(Post.class);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        when(comment.getPost())
                .thenReturn(post);

        when(post.getPostId())
                .thenReturn(actualPostId);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> commentService.updateComment(
                        loginUserId,
                        requestedPostId,
                        commentId,
                        request
                )
        );

        assertEquals(
                "해당 게시글의 댓글이 아닙니다.",
                exception.getMessage()
        );

        verify(comment, never())
                .update(anyString());
    }

    @Test
    void 남의_댓글은_수정할_수_없다() {

        Long loginUserId = 1L;
        Long ownerId = 2L;
        Long postId = 10L;
        Long commentId = 100L;

        CommentRequest request =
                new CommentRequest("수정 내용");

        Comment comment = mock(Comment.class);
        Post post = mock(Post.class);
        User owner = mock(User.class);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        when(comment.getPost())
                .thenReturn(post);

        when(post.getPostId())
                .thenReturn(postId);

        when(comment.getUser())
                .thenReturn(owner);

        when(owner.getUserId())
                .thenReturn(ownerId);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> commentService.updateComment(
                        loginUserId,
                        postId,
                        commentId,
                        request
                )
        );

        assertEquals(
                "본인의 댓글만 수정할 수 있습니다.",
                exception.getMessage()
        );

        verify(comment, never())
                .update(anyString());
    }

    @Test
    void 남의_댓글은_삭제할_수_없다() {

        Long loginUserId = 1L;
        Long ownerId = 2L;
        Long postId = 10L;
        Long commentId = 100L;

        Comment comment = mock(Comment.class);
        Post post = mock(Post.class);
        User owner = mock(User.class);

        when(commentRepository.findById(commentId))
                .thenReturn(Optional.of(comment));

        when(comment.getPost())
                .thenReturn(post);

        when(post.getPostId())
                .thenReturn(postId);

        when(comment.getUser())
                .thenReturn(owner);

        when(owner.getUserId())
                .thenReturn(ownerId);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> commentService.deleteComment(
                        loginUserId,
                        postId,
                        commentId
                )
        );

        assertEquals(
                "본인의 댓글만 삭제할 수 있습니다.",
                exception.getMessage()
        );

        verify(commentRepository, never())
                .delete(comment);
    }
}