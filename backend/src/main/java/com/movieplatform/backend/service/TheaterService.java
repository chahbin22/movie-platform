package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.theater.TheaterResponseDto;
import com.movieplatform.backend.entity.Theater;
import com.movieplatform.backend.repository.TheaterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TheaterService {

    private final TheaterRepository theaterRepository;

    public TheaterService(
            TheaterRepository theaterRepository
    ) {
        this.theaterRepository = theaterRepository;
    }

    @Transactional(readOnly = true)
    public List<TheaterResponseDto> getTheaters() {
        return theaterRepository.findAll()
                .stream()
                .map(TheaterResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TheaterResponseDto getTheater(Long theaterId) {

        Theater theater = theaterRepository.findById(theaterId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "영화관을 찾을 수 없습니다."
                        )
                );

        return TheaterResponseDto.from(theater);
    }
}