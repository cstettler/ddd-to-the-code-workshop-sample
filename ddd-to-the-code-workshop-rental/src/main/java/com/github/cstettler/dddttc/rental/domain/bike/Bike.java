package com.github.cstettler.dddttc.rental.domain.bike;

import com.github.cstettler.dddttc.rental.domain.booking.Booking;
import com.github.cstettler.dddttc.rental.domain.user.User;
import com.github.cstettler.dddttc.rental.domain.user.UserId;
import com.github.cstettler.dddttc.stereotype.Aggregate;
import com.github.cstettler.dddttc.stereotype.AggregateFactory;
import com.github.cstettler.dddttc.stereotype.AggregateId;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;

import java.time.Clock;

import static com.github.cstettler.dddttc.rental.domain.bike.BikeAlreadyBookedException.bikeAlreadyBooked;
import static com.github.cstettler.dddttc.rental.domain.booking.Booking.newBooking;

@Aggregate
public class Bike {

    private final NumberPlate numberPlate;
    private UserId userId;
    private transient final DomainEventPublisher domainEventPublisher;

    private Bike(NumberPlate numberPlate, DomainEventPublisher domainEventPublisher) {
        this.numberPlate = numberPlate;
        this.domainEventPublisher = domainEventPublisher;

        this.userId = null;
    }

    @AggregateId
    public NumberPlate numberPlate() {
        return this.numberPlate;
    }

    public boolean available() {
        return this.userId == null;
    }

    @AggregateFactory(Booking.class)
    public Booking bookBikeFor(User user, Clock clock) throws BikeAlreadyBookedException {
        if (this.userId != null) {
            throw bikeAlreadyBooked(this.numberPlate, this.userId);
        }

        this.userId = user.id();

        return newBooking(this.numberPlate, this.userId, this.domainEventPublisher, clock);
    }

    void markAsReturnedBy(UserId userId) {
        if (!(this.userId.equals(userId))) {
            throw bikeAlreadyBooked(this.numberPlate, this.userId);
        }

        this.userId = null;
    }

    static Bike newBike(NumberPlate numberPlate, DomainEventPublisher domainEventPublisher) {
        return new Bike(numberPlate, domainEventPublisher);
    }

}
