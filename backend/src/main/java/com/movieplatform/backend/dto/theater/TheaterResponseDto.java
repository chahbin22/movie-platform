package com.movieplatform.backend.dto.theater;

import com.movieplatform.backend.entity.Theater;

public record TheaterResponseDto(
        Long theaterId,
        String brand,
        String name,
        String address,
        Double latitude,
        Double longitude
) {

    public static TheaterResponseDto from(Theater theater) {
        return new TheaterResponseDto(
                theater.getTheaterId(),
                theater.getBrand(),
                theater.getName(),
                theater.getAddress(),
                theater.getLatitude(),
                theater.getLongitude()
        );
    }
}