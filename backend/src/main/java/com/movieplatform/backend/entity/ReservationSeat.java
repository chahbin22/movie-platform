package com.movieplatform.backend.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_seats")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_seat_id")
    private Long reservationSeatId;

    @Column(name = "booked_price", nullable = false)
    private Integer bookedPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_seat_id", nullable = false)
    private ScheduleSeat scheduleSeat;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ReservationSeat(
            Reservation reservation,
            ScheduleSeat scheduleSeat,
            Integer bookedPrice
    ) {
        this.reservation = reservation;
        this.scheduleSeat = scheduleSeat;
        this.bookedPrice = bookedPrice;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}