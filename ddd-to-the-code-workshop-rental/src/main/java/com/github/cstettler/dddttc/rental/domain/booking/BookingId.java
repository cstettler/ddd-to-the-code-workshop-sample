package com.github.cstettler.dddttc.rental.domain.booking;

import com.github.cstettler.dddttc.stereotype.ValueObject;

import static java.util.UUID.randomUUID;

@ValueObject
public class BookingId {

    private String value;

    private BookingId() {
        this(randomUUID().toString());
    }

    private BookingId(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static BookingId bookingId(String value) {
        return new BookingId(value);
    }

    static BookingId newBookingId() {
        return new BookingId();
    }

}
