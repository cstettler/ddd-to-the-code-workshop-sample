package com.github.cstettler.dddttc.rental.domain.booking;

import com.github.cstettler.dddttc.rental.domain.bike.NumberPlate;
import com.github.cstettler.dddttc.rental.domain.user.UserId;
import com.github.cstettler.dddttc.stereotype.Aggregate;
import com.github.cstettler.dddttc.stereotype.AggregateFactory;
import com.github.cstettler.dddttc.stereotype.AggregateId;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;

import java.time.Clock;
import java.time.LocalDateTime;

import static com.github.cstettler.dddttc.rental.domain.booking.BikeUsage.bikeUsage;
import static com.github.cstettler.dddttc.rental.domain.booking.BookingAlreadyCompletedException.bookingAlreadyCompleted;
import static com.github.cstettler.dddttc.rental.domain.booking.BookingCompletedEvent.bookingCompleted;
import static com.github.cstettler.dddttc.rental.domain.booking.BookingId.newBookingId;
import static java.time.LocalDateTime.now;

@Aggregate
public class Booking {

    private final BookingId id;
    private final NumberPlate numberPlate;
    private final UserId userId;
    private final LocalDateTime bikeBookedAt;
    private LocalDateTime bikeReturnedAt;
    private boolean completed;
    private transient final DomainEventPublisher domainEventPublisher;

    private Booking(NumberPlate numberPlate, UserId userId, DomainEventPublisher domainEventPublisher, Clock clock) {
        this.numberPlate = numberPlate;
        this.userId = userId;
        this.domainEventPublisher = domainEventPublisher;

        this.id = newBookingId();
        this.bikeBookedAt = now(clock);
        this.bikeReturnedAt = null;
        this.completed = false;
    }

    @AggregateId
    public BookingId id() {
        return this.id;
    }

    public NumberPlate numberPlate() {
        return this.numberPlate;
    }

    public UserId userId() {
        return this.userId;
    }

    public LocalDateTime bikeBookedAt() {
        return this.bikeBookedAt;
    }

    public LocalDateTime bikeReturnedAt() {
        return this.bikeReturnedAt;
    }

    public boolean completed() {
        return this.completed;
    }

    public void returnBike(Clock clock) {
        if (this.completed) {
            throw bookingAlreadyCompleted(this.id);
        }

        this.bikeReturnedAt = now(clock);
        this.completed = true;

        BikeUsage bikeUsage = bikeUsage(this.bikeBookedAt, this.bikeReturnedAt);
        this.domainEventPublisher.publish(bookingCompleted(this.id, this.numberPlate, this.userId, bikeUsage));
    }

    @AggregateFactory(Booking.class)
    public static Booking newBooking(NumberPlate numberPlate, UserId userId, DomainEventPublisher domainEventPublisher, Clock clock) {
        return new Booking(numberPlate, userId, domainEventPublisher, clock);
    }

}
