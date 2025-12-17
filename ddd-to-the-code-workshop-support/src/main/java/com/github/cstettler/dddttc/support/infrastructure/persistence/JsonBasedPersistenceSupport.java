package com.github.cstettler.dddttc.support.infrastructure.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.ANY;
import static com.fasterxml.jackson.annotation.PropertyAccessor.FIELD;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

public abstract class JsonBasedPersistenceSupport {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public JsonBasedPersistenceSupport(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = fieldAccessEnabledObjectMapper();
    }

    protected NamedParameterJdbcTemplate jdbcTemplate() {
        return this.jdbcTemplate;
    }

    protected String serializeToJson(Object object) {
        try {
            return this.objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("unable to serialize object '" + object + "' to json", e);
        }
    }

    protected <T> RowMapper<T> deserializeFromJson(Class<T> type) {
        return (rs, rowNum) -> deserializeJsonValue(rs.getString("data"), type);
    }

    protected  <T> T deserializeJsonValue(String data, Class<T> type) {
        try {
            return this.objectMapper.readValue(data, type);
        } catch (IOException e) {
            throw new IllegalStateException("unable to read json data into object of type '" + type.getName() + "'", e);
        }
    }

    protected static <T extends Collection<?>> T inject(T collectionOfTargets, DomainEventPublisher domainEventPublisher) {
        collectionOfTargets.forEach((target) -> inject(target, domainEventPublisher));

        return collectionOfTargets;
    }

    protected static <T> T inject(T target, DomainEventPublisher domainEventPublisher) {
        try {
            Field domainEventPublisherField = target.getClass().getDeclaredField("domainEventPublisher");
            domainEventPublisherField.setAccessible(true);
            domainEventPublisherField.set(target, domainEventPublisher);
        } catch (Exception e) {
            throw new IllegalStateException("unable to inject domain event publisher into aggregate instance '" + target + "'", e);
        }

        return target;
    }

    private static ObjectMapper fieldAccessEnabledObjectMapper() {
        ObjectMapper objectMapper = new Jackson2ObjectMapperBuilder()
                .featuresToDisable(WRITE_DATES_AS_TIMESTAMPS)
                .build();

        objectMapper.setVisibility(FIELD, ANY);

        return objectMapper;
    }

}
