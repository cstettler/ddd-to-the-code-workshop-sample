package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

@ValueObject
class BookingId {

    private final String value;

    private BookingId(WalletOwner walletOwner, LocalDateTime startedAt) {
        this.value = walletOwner.value() + "-" + startedAt.toEpochSecond(UTC);
    }

    String value() {
        return this.value;
    }

    static BookingId bookingId(WalletOwner walletOwner, LocalDateTime startedAt) {
        return new BookingId(walletOwner, startedAt);
    }

}
