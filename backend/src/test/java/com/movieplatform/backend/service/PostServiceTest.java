package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.post.PostRequest;
import com.movieplatform.backend.dto.post.PostResponseDto;
import com.movieplatform.backend.entity.Post;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.exception.ForbiddenException;
import com.movieplatform.backend.exception.NotFoundException;
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
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostService(
                postRepository,
                userRepository
        );
    }

    @Test
    void 게시글이_존재하지_않으면_NotFoundException() {

        Long postId = 999L;

        when(postRepository.findById(postId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> postService.getPost(postId)
        );

        assertEquals(
                "게시글을 찾을 수 없습니다.",
                exception.getMessage()
        );

        verify(postRepository).findById(postId);
    }

    @Test
    void 게시글_목록이_없으면_빈_목록을_반환한다() {

        when(postRepository.findAllByOrderByCreatedAtDesc())
                .thenReturn(Collections.emptyList());

        List<PostResponseDto> result =
                postService.getPosts();

        assertTrue(result.isEmpty());

        verify(postRepository)
                .findAllByOrderByCreatedAtDesc();
    }

    @Test
    void 남의_게시글은_수정할_수_없다() {

        Long loginUserId = 1L;
        Long ownerId = 2L;
        Long postId = 10L;

        PostRequest request =
                new PostRequest(
                        "수정 제목",
                        "수정 내용"
                );

        User owner = mock(User.class);
        Post post = mock(Post.class);

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        when(post.getUser())
                .thenReturn(owner);

        when(owner.getUserId())
                .thenReturn(ownerId);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> postService.updatePost(
                        loginUserId,
                        postId,
                        request
                )
        );

        assertEquals(
                "본인의 게시글만 수정할 수 있습니다.",
                exception.getMessage()
        );

        verify(post, never())
                .update(anyString(), anyString());
    }

    @Test
    void 남의_게시글은_삭제할_수_없다() {

        Long loginUserId = 1L;
        Long ownerId = 2L;
        Long postId = 10L;

        User owner = mock(User.class);
        Post post = mock(Post.class);

        when(postRepository.findById(postId))
                .thenReturn(Optional.of(post));

        when(post.getUser())
                .thenReturn(owner);

        when(owner.getUserId())
                .thenReturn(ownerId);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> postService.deletePost(
                        loginUserId,
                        postId
                )
        );

        assertEquals(
                "본인의 게시글만 삭제할 수 있습니다.",
                exception.getMessage()
        );

        verify(postRepository, never())
                .delete(post);
    }
}