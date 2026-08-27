package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.screen.ScreenResponseDto;
import com.movieplatform.backend.entity.Screen;
import com.movieplatform.backend.repository.ScreenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ScreenService {

    private final ScreenRepository screenRepository;

    public ScreenService(
            ScreenRepository screenRepository
    ) {
        this.screenRepository = screenRepository;
    }

    @Transactional(readOnly = true)
    public List<ScreenResponseDto> getScreensByTheater(
            Long theaterId
    ) {
        return screenRepository
                .findByTheater_TheaterId(theaterId)
                .stream()
                .map(ScreenResponseDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ScreenResponseDto getScreen(Long screenId) {

        Screen screen = screenRepository.findById(screenId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "상영관을 찾을 수 없습니다."
                        )
                );

        return ScreenResponseDto.from(screen);
    }
}