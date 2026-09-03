package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.post.PostRequest;
import com.movieplatform.backend.dto.post.PostResponseDto;
import com.movieplatform.backend.entity.Post;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.repository.PostRepository;
import com.movieplatform.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(
            PostRepository postRepository,
            UserRepository userRepository
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PostResponseDto createPost(
            Long userId,
            PostRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다."
                        )
                );

        Post post = new Post(
                user,
                request.title(),
                request.content()
        );

        Post savedPost = postRepository.save(post);

        return PostResponseDto.from(savedPost);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPosts() {

        return postRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(PostResponseDto::from)
                .toList();
    }

    @Transactional
    public PostResponseDto getPost(
            Long postId
    ) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "게시글을 찾을 수 없습니다."
                        )
                );

        post.increaseViewCount();

        return PostResponseDto.from(post);
    }

    @Transactional
    public PostResponseDto updatePost(
            Long userId,
            Long postId,
            PostRequest request
    ) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "게시글을 찾을 수 없습니다."
                        )
                );

        if (!post.getUser()
                .getUserId()
                .equals(userId)) {

            throw new IllegalArgumentException(
                    "본인의 게시글만 수정할 수 있습니다."
            );
        }

        post.update(
                request.title(),
                request.content()
        );

        return PostResponseDto.from(post);
    }

    @Transactional
    public void deletePost(
            Long userId,
            Long postId
    ) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "게시글을 찾을 수 없습니다."
                        )
                );

        if (!post.getUser()
                .getUserId()
                .equals(userId)) {

            throw new IllegalArgumentException(
                    "본인의 게시글만 삭제할 수 있습니다."
            );
        }

        postRepository.delete(post);
    }
}