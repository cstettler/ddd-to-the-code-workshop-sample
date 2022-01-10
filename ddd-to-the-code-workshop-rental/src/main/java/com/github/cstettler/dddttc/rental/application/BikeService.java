package com.github.cstettler.dddttc.rental.application;

import com.github.cstettler.dddttc.rental.domain.bike.Bike;
import com.github.cstettler.dddttc.rental.domain.bike.BikeNotExistingException;
import com.github.cstettler.dddttc.rental.domain.bike.BikeRepository;
import com.github.cstettler.dddttc.rental.domain.bike.NumberPlate;
import com.github.cstettler.dddttc.stereotype.ApplicationService;

import java.util.Collection;

@ApplicationService
public class BikeService {

    private final BikeRepository bikeRepository;

    BikeService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    public Collection<Bike> listBikes() {
        return this.bikeRepository.findAll();
    }

    public Bike getBike(NumberPlate numberPlate) throws BikeNotExistingException {
        return this.bikeRepository.get(numberPlate);
    }

}
