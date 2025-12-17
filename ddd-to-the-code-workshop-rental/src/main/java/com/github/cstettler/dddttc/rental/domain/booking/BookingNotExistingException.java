package com.github.cstettler.dddttc.rental.domain.booking;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class BookingNotExistingException extends RuntimeException {

    private BookingNotExistingException(BookingId bookingId) {
        super("booking '" + bookingId.value() + "' does not exist");
    }

    public static BookingNotExistingException bookingNotExisting(BookingId bookingId) {
        return new BookingNotExistingException(bookingId);
    }

}

