package com.github.cstettler.dddttc.rental.domain.bike;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class BikeNotExistingException extends RuntimeException {

    private BikeNotExistingException(NumberPlate numberPlate) {
        super("bike '" + numberPlate.value() + "' does not exist");
    }

    public static BikeNotExistingException bikeNotExisting(NumberPlate numberPlate) {
        return new BikeNotExistingException(numberPlate);
    }

}
