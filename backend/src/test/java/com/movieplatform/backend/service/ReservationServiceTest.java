package com.movieplatform.backend.service;

import com.movieplatform.backend.dto.reservation.ReservationRequest;
import com.movieplatform.backend.entity.Reservation;
import com.movieplatform.backend.entity.ReservationStatus;
import com.movieplatform.backend.entity.Schedule;
import com.movieplatform.backend.entity.ScheduleSeat;
import com.movieplatform.backend.entity.ScheduleSeatStatus;
import com.movieplatform.backend.entity.User;
import com.movieplatform.backend.exception.ConflictException;
import com.movieplatform.backend.exception.ForbiddenException;
import com.movieplatform.backend.exception.NotFoundException;
import com.movieplatform.backend.repository.ReservationRepository;
import com.movieplatform.backend.repository.ReservationSeatRepository;
import com.movieplatform.backend.repository.ScheduleRepository;
import com.movieplatform.backend.repository.ScheduleSeatRepository;
import com.movieplatform.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ReservationSeatRepository reservationSeatRepository;

    @Mock
    private ScheduleSeatRepository scheduleSeatRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private UserRepository userRepository;

    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = new ReservationService(
                reservationRepository,
                reservationSeatRepository,
                scheduleSeatRepository,
                scheduleRepository,
                userRepository
        );
    }

    @Test
    void 사용자가_존재하지_않으면_NotFoundException() {

        Long userId = 999L;

        ReservationRequest request =
                new ReservationRequest(
                        1L,
                        List.of(1L)
                );

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> reservationService.createReservation(
                        userId,
                        request
                )
        );

        assertEquals(
                "사용자를 찾을 수 없습니다.",
                exception.getMessage()
        );

        verifyNoInteractions(scheduleRepository);
        verifyNoInteractions(scheduleSeatRepository);
    }

    @Test
    void 상영일정이_존재하지_않으면_NotFoundException() {

        Long userId = 1L;
        Long scheduleId = 999L;

        ReservationRequest request =
                new ReservationRequest(
                        scheduleId,
                        List.of(1L)
                );

        User user = mock(User.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(scheduleRepository.findById(scheduleId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> reservationService.createReservation(
                        userId,
                        request
                )
        );

        assertEquals(
                "상영 일정을 찾을 수 없습니다.",
                exception.getMessage()
        );

        verifyNoInteractions(scheduleSeatRepository);
    }

    @Test
    void 중복된_좌석이_포함되면_예약할_수_없다() {

        Long userId = 1L;
        Long scheduleId = 10L;

        ReservationRequest request =
                new ReservationRequest(
                        scheduleId,
                        List.of(1L, 1L)
                );

        User user = mock(User.class);
        Schedule schedule = mock(Schedule.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(scheduleRepository.findById(scheduleId))
                .thenReturn(Optional.of(schedule));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        userId,
                        request
                )
        );

        assertEquals(
                "중복된 좌석이 포함되어 있습니다.",
                exception.getMessage()
        );

        verifyNoInteractions(scheduleSeatRepository);
    }

    @Test
    void 존재하지_않는_좌석이_포함되면_NotFoundException() {

        Long userId = 1L;
        Long scheduleId = 10L;

        ReservationRequest request =
                new ReservationRequest(
                        scheduleId,
                        List.of(1L, 2L)
                );

        User user = mock(User.class);
        Schedule schedule = mock(Schedule.class);
        ScheduleSeat scheduleSeat = mock(ScheduleSeat.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(scheduleRepository.findById(scheduleId))
                .thenReturn(Optional.of(schedule));

        when(scheduleSeatRepository
                .findAllByIdsWithLock(List.of(1L, 2L)))
                .thenReturn(List.of(scheduleSeat));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> reservationService.createReservation(
                        userId,
                        request
                )
        );

        assertEquals(
                "존재하지 않는 좌석이 포함되어 있습니다.",
                exception.getMessage()
        );

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }

    @Test
    void 다른_상영일정의_좌석은_예약할_수_없다() {

        Long userId = 1L;
        Long scheduleId = 10L;
        Long anotherScheduleId = 20L;

        ReservationRequest request =
                new ReservationRequest(
                        scheduleId,
                        List.of(1L)
                );

        User user = mock(User.class);
        Schedule schedule = mock(Schedule.class);
        Schedule anotherSchedule = mock(Schedule.class);
        ScheduleSeat scheduleSeat = mock(ScheduleSeat.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(scheduleRepository.findById(scheduleId))
                .thenReturn(Optional.of(schedule));

        when(scheduleSeatRepository
                .findAllByIdsWithLock(List.of(1L)))
                .thenReturn(List.of(scheduleSeat));

        when(schedule.getScheduleId())
                .thenReturn(scheduleId);

        when(scheduleSeat.getSchedule())
                .thenReturn(anotherSchedule);

        when(anotherSchedule.getScheduleId())
                .thenReturn(anotherScheduleId);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reservationService.createReservation(
                        userId,
                        request
                )
        );

        assertEquals(
                "해당 상영 일정의 좌석이 아닙니다.",
                exception.getMessage()
        );

        verify(scheduleSeat, never())
                .reserve();

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }

    @Test
    void 이미_예약된_좌석은_다시_예약할_수_없다() {

        Long userId = 1L;
        Long scheduleId = 10L;

        ReservationRequest request =
                new ReservationRequest(
                        scheduleId,
                        List.of(1L)
                );

        User user = mock(User.class);
        Schedule schedule = mock(Schedule.class);
        ScheduleSeat scheduleSeat = mock(ScheduleSeat.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(scheduleRepository.findById(scheduleId))
                .thenReturn(Optional.of(schedule));

        when(scheduleSeatRepository
                .findAllByIdsWithLock(List.of(1L)))
                .thenReturn(List.of(scheduleSeat));

        when(schedule.getScheduleId())
                .thenReturn(scheduleId);

        when(scheduleSeat.getSchedule())
                .thenReturn(schedule);

        when(scheduleSeat.getStatus())
                .thenReturn(ScheduleSeatStatus.RESERVED);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> reservationService.createReservation(
                        userId,
                        request
                )
        );

        assertEquals(
                "이미 예약된 좌석이 포함되어 있습니다.",
                exception.getMessage()
        );

        verify(scheduleSeat, never())
                .reserve();

        verify(reservationRepository, never())
                .save(any(Reservation.class));
    }

    @Test
    void 남의_예매는_취소할_수_없다() {

        Long loginUserId = 1L;
        Long ownerId = 2L;
        Long reservationId = 100L;

        Reservation reservation =
                mock(Reservation.class);

        User owner = mock(User.class);

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        when(reservation.getUser())
                .thenReturn(owner);

        when(owner.getUserId())
                .thenReturn(ownerId);

        ForbiddenException exception = assertThrows(
                ForbiddenException.class,
                () -> reservationService.cancelReservation(
                        loginUserId,
                        reservationId
                )
        );

        assertEquals(
                "본인의 예매만 취소할 수 있습니다.",
                exception.getMessage()
        );

        verify(reservation, never()).cancel();
        verifyNoInteractions(reservationSeatRepository);
    }

    @Test
    void 이미_취소된_예매는_다시_취소할_수_없다() {

        Long userId = 1L;
        Long reservationId = 100L;

        Reservation reservation =
                mock(Reservation.class);

        User user = mock(User.class);

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.of(reservation));

        when(reservation.getUser())
                .thenReturn(user);

        when(user.getUserId())
                .thenReturn(userId);

        when(reservation.getStatus())
                .thenReturn(ReservationStatus.CANCELED);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> reservationService.cancelReservation(
                        userId,
                        reservationId
                )
        );

        assertEquals(
                "이미 취소된 예매입니다.",
                exception.getMessage()
        );

        verify(reservation, never()).cancel();
        verifyNoInteractions(reservationSeatRepository);
    }

    @Test
    void 존재하지_않는_예매를_취소하면_NotFoundException() {

        Long userId = 1L;
        Long reservationId = 999L;

        when(reservationRepository.findById(reservationId))
                .thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> reservationService.cancelReservation(
                        userId,
                        reservationId
                )
        );

        assertEquals(
                "예매 정보를 찾을 수 없습니다.",
                exception.getMessage()
        );

        verifyNoInteractions(reservationSeatRepository);
    }
}