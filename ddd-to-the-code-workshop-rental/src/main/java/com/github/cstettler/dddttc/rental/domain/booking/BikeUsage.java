package com.github.cstettler.dddttc.rental.domain.booking;

import com.github.cstettler.dddttc.stereotype.ValueObject;

import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

@ValueObject
public class BikeUsage {

    private final LocalDateTime startedAt;
    private final LocalDateTime endedAt;
    private final long durationInSeconds;

    private BikeUsage(LocalDateTime startedAt, LocalDateTime endedAt) {
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.durationInSeconds = usageTimeInSeconds(startedAt, endedAt);
    }

    private long usageTimeInSeconds(LocalDateTime startedAt, LocalDateTime endedAt) {
        return endedAt.toEpochSecond(UTC) - startedAt.toEpochSecond(UTC);
    }

    static BikeUsage bikeUsage(LocalDateTime startedAt, LocalDateTime endedAt) {
        return new BikeUsage(startedAt, endedAt);
    }

}
