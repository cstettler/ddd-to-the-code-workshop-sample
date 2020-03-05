package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

@ValueObject
class BookingId {

    private final String value;

    private BookingId(UserId userId, LocalDateTime startedAt) {
        this.value = userId.value() + "-" + startedAt.toEpochSecond(UTC);
    }

    String value() {
        return this.value;
    }

    static BookingId bookingId(UserId userId, LocalDateTime startedAt) {
        return new BookingId(userId, startedAt);
    }

}
