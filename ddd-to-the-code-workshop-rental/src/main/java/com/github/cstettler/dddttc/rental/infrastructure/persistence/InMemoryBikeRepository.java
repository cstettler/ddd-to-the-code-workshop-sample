package com.github.cstettler.dddttc.rental.infrastructure.persistence;

import com.github.cstettler.dddttc.rental.domain.bike.Bike;
import com.github.cstettler.dddttc.rental.domain.bike.BikeRepository;
import com.github.cstettler.dddttc.rental.domain.bike.NumberPlate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.github.cstettler.dddttc.rental.domain.bike.BikeNotExistingException.bikeNotExisting;

class InMemoryBikeRepository implements BikeRepository {

    private final Map<NumberPlate, Bike> bikeByNumberPlate;

    InMemoryBikeRepository() {
        this.bikeByNumberPlate = new HashMap<>();
    }

    @Override
    public void add(Bike bike) {
        this.bikeByNumberPlate.put(bike.numberPlate(), bike);
    }

    @Override
    public Bike get(NumberPlate numberPlate) {
        Bike bike = this.bikeByNumberPlate.get(numberPlate);

        if (bike == null) {
            throw bikeNotExisting(numberPlate);
        }

        return bike;
    }

    @Override
    public Collection<Bike> findAll() {
        return this.bikeByNumberPlate.values();
    }

}
