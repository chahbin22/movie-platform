package com.movieplatform.backend.exception;

public record ErrorResponse(
        int status,
        String message
) {
}