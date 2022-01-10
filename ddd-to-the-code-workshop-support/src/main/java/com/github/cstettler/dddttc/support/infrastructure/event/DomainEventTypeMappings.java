package com.github.cstettler.dddttc.support.infrastructure.event;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;

public class DomainEventTypeMappings {

    private final Map<Class<?>, String> mappings;

    private DomainEventTypeMappings(Map<Class<?>, String> mappings) {
        this.mappings = unmodifiableMap(mappings);
    }

    String getDomainEventTypeIfAvailable(Class<?> domainEventClass) {
        return this.mappings.get(domainEventClass);
    }

    public static Builder domainEventTypeMappings() {
        return new Builder();
    }


    public static class Builder {

        private final Map<Class<?>, String> mappings;

        private Builder() {
            this.mappings = new HashMap<>();
        }

        public Builder addMapping(Class<?> domainEventClass, String domainEventType) {
            this.mappings.put(domainEventClass, domainEventType);

            return this;
        }

        public DomainEventTypeMappings build() {
            return new DomainEventTypeMappings(this.mappings);
        }

    }

}
