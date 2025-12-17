package com.github.cstettler.dddttc.accounting.infrastructure.event;

import com.github.cstettler.dddttc.accounting.application.WalletService;
import com.github.cstettler.dddttc.accounting.domain.Booking;
import com.github.cstettler.dddttc.accounting.domain.BookingAlreadyBilledException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static com.github.cstettler.dddttc.accounting.domain.Booking.booking;
import static com.github.cstettler.dddttc.accounting.domain.WalletOwner.walletOwner;

@Component
class BookingCompletedMessageListener {

    private static Logger LOGGER = LoggerFactory.getLogger(BookingCompletedMessageListener.class);

    private final WalletService walletService;

    BookingCompletedMessageListener(WalletService walletService) {
        this.walletService = walletService;
    }

    @JmsListener(destination = "rental/booking-completed")
    public void onUserRegistrationCompleted(BookingCompletedMessage message) {
        String userIdValue = message.userId.value;
        LocalDateTime startedAt = message.bikeUsage.startedAt;
        long durationInSeconds = message.bikeUsage.durationInSeconds;

        LOGGER.info("received booking completed message for user '" + userIdValue + "'");

        try {
            Booking booking = booking(walletOwner(userIdValue), startedAt, durationInSeconds);

            this.walletService.billBookingFee(booking);

            LOGGER.info("booked rental fee for usage of '" + booking.durationInSeconds() + "' seconds to wallet of owner '" + userIdValue + "'");
        } catch (BookingAlreadyBilledException e) {
            // ignored
        }
    }


    public static class BookingCompletedMessage {

        UserId userId;
        BikeUsage bikeUsage;
    }


    public static class UserId {

        String value;
    }


    public static class BikeUsage {

        LocalDateTime startedAt;
        long durationInSeconds;

    }

}
