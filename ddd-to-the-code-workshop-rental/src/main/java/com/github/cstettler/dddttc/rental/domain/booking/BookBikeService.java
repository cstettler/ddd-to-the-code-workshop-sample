package com.github.cstettler.dddttc.rental.domain.booking;

import com.github.cstettler.dddttc.rental.domain.bike.Bike;
import com.github.cstettler.dddttc.rental.domain.bike.BikeAlreadyBookedException;
import com.github.cstettler.dddttc.rental.domain.bike.BikeNotExistingException;
import com.github.cstettler.dddttc.rental.domain.bike.BikeRepository;
import com.github.cstettler.dddttc.rental.domain.bike.NumberPlate;
import com.github.cstettler.dddttc.rental.domain.user.User;
import com.github.cstettler.dddttc.rental.domain.user.UserId;
import com.github.cstettler.dddttc.rental.domain.user.UserNotExistingException;
import com.github.cstettler.dddttc.rental.domain.user.UserRepository;
import com.github.cstettler.dddttc.stereotype.DomainService;

import java.time.Clock;

@DomainService
public class BookBikeService {

    private final BikeRepository bikeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final Clock clock;

    public BookBikeService(BikeRepository bikeRepository, BookingRepository bookingRepository, UserRepository userRepository, Clock clock) {
        this.bikeRepository = bikeRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.clock = clock;
    }

    public void bookBike(NumberPlate numberPlate, UserId userId) throws BikeNotExistingException, UserNotExistingException, BikeAlreadyBookedException {
        Bike bike = this.bikeRepository.get(numberPlate);
        User user = this.userRepository.get(userId);

        Booking booking = bike.bookBikeFor(user, this.clock);
        this.bikeRepository.add(bike);
        this.bookingRepository.add(booking);
    }

}
