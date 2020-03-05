package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class BookingAlreadyBilledException extends RuntimeException {

    private BookingAlreadyBilledException(Wallet wallet, Booking booking) {
        super("booking '" + booking.id().value() + " has already been billed to wallet '" + wallet.id().value() + "'");
    }

    static BookingAlreadyBilledException bookingAlreadyBilled(Wallet wallet, Booking booking) {
        return new BookingAlreadyBilledException(wallet, booking);
    }

}
