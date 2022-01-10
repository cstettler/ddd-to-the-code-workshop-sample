package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.DomainService;

import java.math.BigDecimal;

import static com.github.cstettler.dddttc.accounting.domain.Amount.amount;
import static java.lang.Math.round;
import static java.math.BigDecimal.valueOf;

@DomainService
public class BookingFeePolicy {

    private final BigDecimal initialPrice;
    private final BigDecimal pricePerMinute;

    BookingFeePolicy() {
        this.initialPrice = new BigDecimal("1.50");
        this.pricePerMinute = new BigDecimal("0.25");
    }

    Amount feeForBooking(Booking booking) {
        int roundedMinutes = round((float) booking.durationInSeconds() / 60);

        return amount(this.initialPrice.add(this.pricePerMinute.multiply(valueOf(roundedMinutes))));
    }

}
