package com.github.cstettler.dddttc.rental.domain.bike;

import com.github.cstettler.dddttc.rental.domain.user.UserId;
import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class BikeAlreadyBookedException extends RuntimeException {

    private BikeAlreadyBookedException(NumberPlate numberPlate, UserId userId) {
        super("bike '" + numberPlate.value() + "' is already booked by user '" + userId.value() + "'");
    }

    static BikeAlreadyBookedException bikeAlreadyBooked(NumberPlate numberPlate, UserId userId) {
        return new BikeAlreadyBookedException(numberPlate, userId);
    }

}
