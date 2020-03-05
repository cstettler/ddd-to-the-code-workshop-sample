package com.github.cstettler.dddttc.rental.domain.booking;

import com.github.cstettler.dddttc.rental.domain.bike.NumberPlate;
import com.github.cstettler.dddttc.rental.domain.user.UserId;
import com.github.cstettler.dddttc.stereotype.DomainEvent;

@DomainEvent
public class BookingCompletedEvent {

    private final BookingId bookingId;
    private final NumberPlate numberPlate;
    private final UserId userId;
    private final BikeUsage bikeUsage;

    private BookingCompletedEvent(BookingId bookingId, NumberPlate numberPlate, UserId userId, BikeUsage bikeUsage) {
        this.bookingId = bookingId;
        this.numberPlate = numberPlate;
        this.userId = userId;
        this.bikeUsage = bikeUsage;
    }

    BookingId bookingId() {
        return this.bookingId;
    }

    public NumberPlate numberPlate() {
        return this.numberPlate;
    }

    public UserId userId() {
        return this.userId;
    }

    BikeUsage bikeUsage() {
        return this.bikeUsage;
    }

    static BookingCompletedEvent bookingCompleted(BookingId bookingId, NumberPlate numberPlate, UserId userId, BikeUsage bikeUsage) {
        return new BookingCompletedEvent(bookingId, numberPlate, userId, bikeUsage);
    }

}
