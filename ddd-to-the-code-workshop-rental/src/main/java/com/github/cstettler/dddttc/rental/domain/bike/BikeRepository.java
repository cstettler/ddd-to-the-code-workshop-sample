package com.github.cstettler.dddttc.rental.domain.bike;

import com.github.cstettler.dddttc.stereotype.Repository;

import java.util.Collection;

@Repository
public interface BikeRepository {

    void add(Bike bike);

    Bike get(NumberPlate numberPlate);

    Collection<Bike> findAll();

}
