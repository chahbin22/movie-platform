package com.movieplatform.backend.dto.screen;

import com.movieplatform.backend.entity.Screen;

public record ScreenResponseDto(
        Long screenId,
        String name,
        Long theaterId,
        String theaterName
) {

    public static ScreenResponseDto from(Screen screen) {
        return new ScreenResponseDto(
                screen.getScreenId(),
                screen.getName(),
                screen.getTheater().getTheaterId(),
                screen.getTheater().getName()
        );
    }
}