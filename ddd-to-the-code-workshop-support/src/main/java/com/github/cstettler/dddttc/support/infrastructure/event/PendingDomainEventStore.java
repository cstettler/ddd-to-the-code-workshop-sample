package com.github.cstettler.dddttc.support.infrastructure.event;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonMap;

class PendingDomainEventStore {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    PendingDomainEventStore(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    void storePendingDomainEvent(PendingDomainEvent pendingDomainEvent) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", pendingDomainEvent.id());
        params.put("type", pendingDomainEvent.type());
        params.put("payload", pendingDomainEvent.payload());
        params.put("publishedAt", pendingDomainEvent.publishedAt());

        this.jdbcTemplate.update(
                "INSERT INTO domain_event (id, type, payload, published_at) VALUES(:id, :type, :payload, :publishedAt)",
                params
        );
    }

    List<PendingDomainEvent> loadNextPendingDomainEvents(int count) {
        return this.jdbcTemplate.query(
                "SELECT id, type, payload, published_at FROM domain_event WHERE dispatched = 0 ORDER BY published_at ASC LIMIT :count",
                singletonMap("count", count),
                pendingDomainEventRowMapper()
        );
    }

    void markPendingDomainEventDispatched(PendingDomainEvent pendingDomainEvent) {
        this.jdbcTemplate.update(
                "UPDATE domain_event SET dispatched = 1 WHERE id = :id",
                singletonMap("id", pendingDomainEvent.id())
        );
    }

    private RowMapper<PendingDomainEvent> pendingDomainEventRowMapper() {
        return (resultSet, i) -> {
            String id = resultSet.getString("id");
            String type = resultSet.getString("type");
            String payload = resultSet.getString("payload");
            LocalDateTime publishedAt = resultSet.getTimestamp("published_at").toLocalDateTime();

            return new PendingDomainEvent(id, type, payload, publishedAt);
        };
    }

}
