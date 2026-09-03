package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.reservation.ReservationRequest;
import com.movieplatform.backend.dto.reservation.ReservationResponseDto;
import com.movieplatform.backend.entity.*;
import com.movieplatform.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSeatRepository reservationSeatRepository;
    private final ScheduleSeatRepository scheduleSeatRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationSeatRepository reservationSeatRepository,
            ScheduleSeatRepository scheduleSeatRepository,
            ScheduleRepository scheduleRepository,
            UserRepository userRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationSeatRepository = reservationSeatRepository;
        this.scheduleSeatRepository = scheduleSeatRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponseDto createReservation(
            Long userId,
            ReservationRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "사용자를 찾을 수 없습니다."
                        )
                );

        Schedule schedule =
                scheduleRepository
                        .findById(request.scheduleId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "상영 일정을 찾을 수 없습니다."
                                )
                        );

        List<Long> scheduleSeatIds =
                request.scheduleSeatIds()
                        .stream()
                        .distinct()
                        .sorted()
                        .toList();

        if (scheduleSeatIds.size()
                != request.scheduleSeatIds().size()) {

            throw new IllegalArgumentException(
                    "중복된 좌석이 포함되어 있습니다."
            );
        }

        List<ScheduleSeat> scheduleSeats =
                scheduleSeatRepository
                        .findAllByIdsWithLock(
                                scheduleSeatIds
                        );

        if (scheduleSeats.size()
                != scheduleSeatIds.size()) {

            throw new IllegalArgumentException(
                    "존재하지 않는 좌석이 포함되어 있습니다."
            );
        }

        for (ScheduleSeat scheduleSeat : scheduleSeats) {

            if (!scheduleSeat
                    .getSchedule()
                    .getScheduleId()
                    .equals(schedule.getScheduleId())) {

                throw new IllegalArgumentException(
                        "해당 상영 일정의 좌석이 아닙니다."
                );
            }
        }

        int totalPrice = scheduleSeats.stream()
                .mapToInt(ScheduleSeat::getPrice)
                .sum();

        Reservation reservation =
                new Reservation(
                        user,
                        schedule,
                        totalPrice
                );

        reservationRepository.save(reservation);

        for (ScheduleSeat scheduleSeat : scheduleSeats) {
            scheduleSeat.reserve();
        }

        List<ReservationSeat> reservationSeats =
                scheduleSeats.stream()
                        .map(scheduleSeat ->
                                new ReservationSeat(
                                        reservation,
                                        scheduleSeat,
                                        scheduleSeat.getPrice()
                                )
                        )
                        .toList();

        reservationSeatRepository
                .saveAll(reservationSeats);

        return ReservationResponseDto.from(
                reservation,
                reservationSeats
        );
    }
}