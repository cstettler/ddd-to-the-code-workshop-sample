package com.github.cstettler.dddttc.support.domain;

import com.github.cstettler.dddttc.stereotype.InfrastructureService;

@InfrastructureService
public interface DomainEventPublisher {

    void publish(Object domainEvent);

}
