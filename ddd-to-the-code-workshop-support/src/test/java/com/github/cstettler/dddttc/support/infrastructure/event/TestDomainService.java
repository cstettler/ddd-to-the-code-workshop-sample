package com.github.cstettler.dddttc.support.infrastructure.event;

import com.github.cstettler.dddttc.stereotype.DomainEventHandler;
import com.github.cstettler.dddttc.stereotype.DomainService;

import java.util.ArrayList;
import java.util.List;

@DomainService
class TestDomainService {

    private List<TestDomainEvent> recordedDomainEvents;

    TestDomainService() {
        this.recordedDomainEvents = new ArrayList<>();
    }

    @DomainEventHandler
    void handleTestDomainEventWithImplicitType(TestDomainEvent event) {
        this.recordedDomainEvents.add(event);
    }

    List<TestDomainEvent> recordedDomainEvents() {
        return this.recordedDomainEvents;
    }

    void clearRecordedDomainEvents() {
        this.recordedDomainEvents.clear();
    }

}
