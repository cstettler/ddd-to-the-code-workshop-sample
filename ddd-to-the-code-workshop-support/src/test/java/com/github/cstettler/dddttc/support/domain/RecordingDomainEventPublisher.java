package com.github.cstettler.dddttc.support.domain;

import com.github.cstettler.dddttc.support.EnableComponentScanExclusions.ExcludeFromComponentScan;

import java.util.ArrayList;
import java.util.List;

@ExcludeFromComponentScan
public class RecordingDomainEventPublisher implements DomainEventPublisher {

    private final List<Object> recordedDomainEvents;

    private RecordingDomainEventPublisher() {
        this.recordedDomainEvents = new ArrayList<>();
    }

    @Override
    public void publish(Object domainEvent) {
        this.recordedDomainEvents.add(domainEvent);
    }

    public int numberOfRecordedDomainEvents() {
        return this.recordedDomainEvents.size();
    }

    public <T> T singleRecordedDomainEvent() {
        return recordedDomainEvent(0);
    }

    @SuppressWarnings("unchecked")
    public <T> T recordedDomainEvent(int index) {
        return (T) this.recordedDomainEvents.get(index);
    }

    public void clearRecordedDomainEvents() {
        this.recordedDomainEvents.clear();
    }

    public static RecordingDomainEventPublisher recordingDomainEventPublisher() {
        return new RecordingDomainEventPublisher();
    }

}
