package com.github.cstettler.dddttc.registration.infrastructure.persistence;

import com.github.cstettler.dddttc.registration.domain.UserHandle;
import com.github.cstettler.dddttc.registration.domain.UserHandleAlreadyInUseException;
import com.github.cstettler.dddttc.registration.domain.UserRegistration;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationId;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationNotExistingException;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationRepository;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;
import com.github.cstettler.dddttc.support.infrastructure.persistence.JsonBasedPersistenceSupport;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.github.cstettler.dddttc.registration.domain.UserHandleAlreadyInUseException.userHandleAlreadyInUse;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationNotExistingException.userRegistrationNotExisting;
import static java.util.Collections.singletonMap;

class JdbcUserRegistrationRepository extends JsonBasedPersistenceSupport implements UserRegistrationRepository {

    private final DomainEventPublisher domainEventPublisher;

    JdbcUserRegistrationRepository(NamedParameterJdbcTemplate jdbcTemplate, DomainEventPublisher domainEventPublisher) {
        super(jdbcTemplate);

        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public void add(UserRegistration userRegistration) throws UserHandleAlreadyInUseException {
        try {
            insert(userRegistration);
        } catch (DuplicateKeyException e) {
            throw userHandleAlreadyInUse(userRegistration.userHandle());
        }
    }

    @Override
    public void update(UserRegistration userRegistration) throws UserRegistrationNotExistingException {
        if (delete(userRegistration.id())) {
            insert(userRegistration);
        } else {
            throw userRegistrationNotExisting(userRegistration.id());
        }
    }

    @Override
    public UserRegistration get(UserRegistrationId userRegistrationId) throws UserRegistrationNotExistingException {
        try {
            return inject(jdbcTemplate().queryForObject(
                    "SELECT data FROM user_registration WHERE id = :id",
                    singletonMap("id", userRegistrationId.value()),
                    deserializeFromJson(UserRegistration.class)
            ), this.domainEventPublisher);
        } catch (EmptyResultDataAccessException e) {
            throw userRegistrationNotExisting(userRegistrationId);
        }
    }

    @Override
    public UserRegistration find(UserHandle userHandle) {
        try {
            return inject(jdbcTemplate().queryForObject(
                    "SELECT data FROM user_registration WHERE user_handle = :userHandle",
                    singletonMap("userHandle", userHandle.value()),
                    deserializeFromJson(UserRegistration.class)
            ), this.domainEventPublisher);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private void insert(UserRegistration userRegistration) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", userRegistration.id().value());
        params.put("userHandle", userRegistration.userHandle().value());
        params.put("data", serializeToJson(userRegistration));

        jdbcTemplate().update("INSERT INTO user_registration (id, user_handle, data) VALUES (:id, :userHandle, :data)", params);
    }

    private boolean delete(UserRegistrationId userRegistrationId) {
        int updateCount = jdbcTemplate().update("DELETE FROM user_registration WHERE id = :id", singletonMap("id", userRegistrationId.value()));

        return updateCount == 1;
    }

}
