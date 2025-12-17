package com.github.cstettler.dddttc.rental.infrastructure.persistence;

import com.github.cstettler.dddttc.rental.domain.booking.Booking;
import com.github.cstettler.dddttc.rental.domain.booking.BookingId;
import com.github.cstettler.dddttc.rental.domain.booking.BookingNotExistingException;
import com.github.cstettler.dddttc.rental.domain.booking.BookingRepository;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;
import com.github.cstettler.dddttc.support.infrastructure.persistence.JsonBasedPersistenceSupport;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.github.cstettler.dddttc.rental.domain.booking.BookingNotExistingException.bookingNotExisting;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

class JdbcBookingRepository extends JsonBasedPersistenceSupport implements BookingRepository {

    private final DomainEventPublisher domainEventPublisher;

    JdbcBookingRepository(NamedParameterJdbcTemplate jdbcTemplate, DomainEventPublisher domainEventPublisher) {
        super(jdbcTemplate);

        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public void add(Booking booking) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", booking.id().value());
        params.put("data", serializeToJson(booking));

        jdbcTemplate().update("INSERT INTO booking (id, data) VALUES (:id, :data)", params);
    }

    @Override
    public void update(Booking booking) {
        jdbcTemplate().update("DELETE FROM booking WHERE id = :id", singletonMap("id", booking.id().value()));

        Map<String, Object> params = new HashMap<>();
        params.put("id", booking.id().value());
        params.put("data", serializeToJson(booking));

        jdbcTemplate().update("INSERT INTO booking (id, data) VALUES (:id, :data)", params);
    }

    @Override
    public Booking get(BookingId bookingId) throws BookingNotExistingException {
        try {
            return inject(jdbcTemplate().queryForObject(
                    "SELECT data FROM booking WHERE id = :id",
                    singletonMap("id", bookingId.value()),
                    deserializeFromJson(Booking.class)
            ), this.domainEventPublisher);
        } catch (EmptyResultDataAccessException e) {
            throw bookingNotExisting(bookingId);
        }
    }

    @Override
    public Collection<Booking> findAll() {
        return inject(jdbcTemplate().query(
                "SELECT data FROM booking",
                emptyMap(),
                deserializeFromJson(Booking.class)
        ), this.domainEventPublisher);
    }

}
