package com.github.cstettler.dddttc.rental.infrastructure.persistence;

import com.github.cstettler.dddttc.rental.domain.user.User;
import com.github.cstettler.dddttc.rental.domain.user.UserAlreadyExistsException;
import com.github.cstettler.dddttc.rental.domain.user.UserId;
import com.github.cstettler.dddttc.rental.domain.user.UserNotExistingException;
import com.github.cstettler.dddttc.rental.domain.user.UserRepository;
import com.github.cstettler.dddttc.support.infrastructure.persistence.JsonBasedPersistenceSupport;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.HashMap;
import java.util.Map;

import static com.github.cstettler.dddttc.rental.domain.user.UserAlreadyExistsException.userAlreadyExists;
import static com.github.cstettler.dddttc.rental.domain.user.UserNotExistingException.userNotExisting;
import static java.util.Collections.singletonMap;

class JdbcUserRepository extends JsonBasedPersistenceSupport implements UserRepository {

    JdbcUserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void add(User user) throws UserAlreadyExistsException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", user.id().value());
            params.put("data", serializeToJson(user));

            jdbcTemplate().update("INSERT INTO user (id, data) VALUES (:id, :data)", params);
        } catch (DuplicateKeyException e) {
            throw userAlreadyExists(user.id());
        }
    }

    @Override
    public User get(UserId userId) throws UserNotExistingException {
        try {
            return jdbcTemplate().queryForObject(
                    "SELECT data FROM user WHERE id = :id",
                    singletonMap("id", userId.value()),
                    deserializeFromJson(User.class)
            );
        } catch (EmptyResultDataAccessException e) {
            throw userNotExisting(userId);
        }
    }

}
