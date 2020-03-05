package com.github.cstettler.dddttc.rental.domain.bike;

import com.github.cstettler.dddttc.stereotype.DomainService;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;

import static com.github.cstettler.dddttc.rental.domain.bike.Bike.newBike;
import static com.github.cstettler.dddttc.rental.domain.bike.NumberPlate.numberPlate;

@DomainService
public class InitializeBikesService {

    private final BikeRepository bikeRepository;
    private final DomainEventPublisher domainEventPublisher;

    InitializeBikesService(BikeRepository bikeRepository, DomainEventPublisher domainEventPublisher) {
        this.bikeRepository = bikeRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    public void initializeBikes() {
        this.bikeRepository.add(newBike(numberPlate("ZH-123"), this.domainEventPublisher));
        this.bikeRepository.add(newBike(numberPlate("ZH-987"), this.domainEventPublisher));
        this.bikeRepository.add(newBike(numberPlate("ZH-666"), this.domainEventPublisher));
    }

}
