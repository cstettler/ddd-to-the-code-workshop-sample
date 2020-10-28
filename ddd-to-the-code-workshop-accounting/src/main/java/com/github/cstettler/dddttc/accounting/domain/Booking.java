package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

import java.time.LocalDateTime;

import static com.github.cstettler.dddttc.accounting.domain.BookingId.bookingId;

@ValueObject
public class Booking {

    private final BookingId id;
    private final WalletOwner walletOwner;
    private final long durationInSeconds;

    private Booking(WalletOwner walletOwner, LocalDateTime startedAt, long durationInSeconds) {
        this.id = bookingId(walletOwner, startedAt);
        this.walletOwner = walletOwner;
        this.durationInSeconds = durationInSeconds;
    }

    public BookingId id() {
        return this.id;
    }

    public WalletOwner walletOwner() {
        return this.walletOwner;
    }

    public long durationInSeconds() {
        return this.durationInSeconds;
    }

    public static Booking booking(WalletOwner walletOwner, LocalDateTime startedAt, long durationInSeconds) {
        return new Booking(walletOwner, startedAt, durationInSeconds);
    }

}
