package com.github.cstettler.dddttc.accounting.infrastructure.persistence;

import com.github.cstettler.dddttc.accounting.domain.UserId;
import com.github.cstettler.dddttc.accounting.domain.Wallet;
import com.github.cstettler.dddttc.accounting.domain.WalletAlreadyExistsException;
import com.github.cstettler.dddttc.accounting.domain.WalletNotExistingException;
import com.github.cstettler.dddttc.accounting.domain.WalletRepository;
import com.github.cstettler.dddttc.support.infrastructure.persistence.JsonBasedPersistenceSupport;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.github.cstettler.dddttc.accounting.domain.WalletAlreadyExistsException.walletAlreadyExists;
import static com.github.cstettler.dddttc.accounting.domain.WalletNotExistingException.walletNotExisting;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

class JdbcWalletRepository extends JsonBasedPersistenceSupport implements WalletRepository {

    JdbcWalletRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    public void add(Wallet wallet) throws WalletAlreadyExistsException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("id", wallet.id().value());
            params.put("data", serializeToJson(wallet));

            jdbcTemplate().update("INSERT INTO wallet (id, data) VALUES (:id, :data)", params);
        } catch (DuplicateKeyException e) {
            throw walletAlreadyExists(wallet.id());
        }
    }

    @Override
    public void update(Wallet wallet) throws WalletNotExistingException {
        jdbcTemplate().update("DELETE FROM wallet WHERE id = :id", singletonMap("id", wallet.id().value()));

        Map<String, Object> params = new HashMap<>();
        params.put("id", wallet.id().value());
        params.put("data", serializeToJson(wallet));

        jdbcTemplate().update("INSERT INTO wallet (id, data) VALUES (:id, :data)", params);
    }

    @Override
    public Wallet get(UserId userId) throws WalletNotExistingException {
        try {
            return jdbcTemplate().queryForObject(
                    "SELECT data FROM wallet WHERE id = :id",
                    singletonMap("id", userId.value()),
                    deserializeFromJson(Wallet.class)
            );
        } catch (EmptyResultDataAccessException e) {
            throw walletNotExisting(userId);
        }
    }

    @Override
    public Collection<Wallet> findAll() {
        return jdbcTemplate().query(
                "SELECT data FROM wallet",
                emptyMap(),
                deserializeFromJson(Wallet.class)
        );
    }

}
