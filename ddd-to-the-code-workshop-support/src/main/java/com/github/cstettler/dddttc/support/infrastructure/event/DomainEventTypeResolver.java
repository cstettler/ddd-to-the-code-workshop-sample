package com.github.cstettler.dddttc.support.infrastructure.event;

import java.util.List;

class DomainEventTypeResolver {

    private final List<DomainEventTypeMappings> allDomainEventTypeMappings;

    DomainEventTypeResolver(List<DomainEventTypeMappings> allDomainEventTypeMappings) {
        this.allDomainEventTypeMappings = allDomainEventTypeMappings;
    }

    String resolveDomainEventType(Class<?> domainEventClass) {
        for (DomainEventTypeMappings domainEventTypeMappings : this.allDomainEventTypeMappings) {
            String domainEventType = domainEventTypeMappings.getDomainEventTypeIfAvailable(domainEventClass);

            if (domainEventType != null) {
                return domainEventType;
            }
        }

        return domainEventClass.getName();
    }

}
