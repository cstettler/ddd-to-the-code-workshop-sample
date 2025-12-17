package com.github.cstettler.dddttc.support.infrastructure.event;

import com.github.cstettler.dddttc.stereotype.DomainEvent;

@DomainEvent
class TestDomainEvent {

    private String value;

    private TestDomainEvent(String value) {
        this.value = value;
    }

    String value() {
        return this.value;
    }

    static TestDomainEvent testDomainEvent(String value) {
        return new TestDomainEvent(value);
    }

}
