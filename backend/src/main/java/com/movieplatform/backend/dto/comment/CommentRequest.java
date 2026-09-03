package com.movieplatform.backend.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentRequest(

        @NotBlank
        @Size(max = 1000)
        String content

) {
}