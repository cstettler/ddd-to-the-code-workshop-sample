package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

import java.time.LocalDateTime;

import static com.github.cstettler.dddttc.accounting.domain.BookingId.bookingId;

@ValueObject
public class Booking {

    private final BookingId id;
    private final UserId userId;
    private final long durationInSeconds;

    private Booking(UserId userId, LocalDateTime startedAt, long durationInSeconds) {
        this.id = bookingId(userId, startedAt);
        this.userId = userId;
        this.durationInSeconds = durationInSeconds;
    }

    public BookingId id() {
        return this.id;
    }

    public UserId userId() {
        return this.userId;
    }

    public long durationInSeconds() {
        return this.durationInSeconds;
    }

    public static Booking booking(UserId userId, LocalDateTime startedAt, long durationInSeconds) {
        return new Booking(userId, startedAt, durationInSeconds);
    }

}
