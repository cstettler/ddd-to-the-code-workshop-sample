package com.github.cstettler.dddttc.rental.domain.booking;

import com.github.cstettler.dddttc.stereotype.Repository;

import java.util.Collection;

@Repository
public interface BookingRepository {

    void add(Booking booking);

    void update(Booking booking);

    Booking get(BookingId bookingId) throws BookingNotExistingException;

    Collection<Booking> findAll();

}
