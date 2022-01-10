package com.github.cstettler.dddttc.support.infrastructure.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

class DomainEventSerializer {

    private final ObjectMapper objectMapper;

    DomainEventSerializer() {
        this.objectMapper = fieldAccessEnabledObjectMapper();
    }

    String serialize(Object domainEvent) {
        try {
            return this.objectMapper.writeValueAsString(domainEvent);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("unable to serialize domain event '" + domainEvent + "'", e);
        }
    }

    <T> T deserialize(String payload, Class<T> type) {
        try {
            return this.objectMapper.readValue(payload, type);
        } catch (IOException e) {
            throw new IllegalStateException("unable to deserialize domain event payload '" + payload + "' to type '" + type.getName() + "'", e);
        }
    }

    private static ObjectMapper fieldAccessEnabledObjectMapper() {
        ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
                .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
                .build();

        objectMapper.setVisibility(FIELD, ANY);

        return objectMapper;
    }

}
