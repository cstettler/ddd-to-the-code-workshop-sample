package com.github.cstettler.dddttc.rental.domain.bike;

import com.github.cstettler.dddttc.rental.domain.booking.BookingCompletedEvent;
import com.github.cstettler.dddttc.rental.domain.user.UserId;
import com.github.cstettler.dddttc.stereotype.DomainEventHandler;
import com.github.cstettler.dddttc.stereotype.DomainService;

@DomainService
class ReleaseBikeService {

    private final BikeRepository bikeRepository;

    ReleaseBikeService(BikeRepository bikeRepository) {
        this.bikeRepository = bikeRepository;
    }

    @DomainEventHandler
    void releaseBike(BookingCompletedEvent event) {
        NumberPlate numberPlate = event.numberPlate();
        UserId lastUserId = event.userId();

        Bike bike = this.bikeRepository.get(numberPlate);
        bike.markAsReturnedBy(lastUserId);
    }

}
