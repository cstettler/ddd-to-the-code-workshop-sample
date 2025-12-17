package com.github.cstettler.dddttc.support.infrastructure.event;

import java.time.LocalDateTime;

class PendingDomainEvent {

    private final String id;
    private final String type;
    private final String payload;
    private final LocalDateTime publishedAt;

    PendingDomainEvent(String id, String type, String payload, LocalDateTime publishedAt) {
        this.id = id;
        this.type = type;
        this.payload = payload;
        this.publishedAt = publishedAt;
    }

    String id() {
        return this.id;
    }

    String type() {
        return this.type;
    }

    String payload() {
        return this.payload;
    }

    LocalDateTime publishedAt() {
        return this.publishedAt;
    }

}
