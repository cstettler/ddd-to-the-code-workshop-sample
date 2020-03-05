package com.github.cstettler.dddttc.accounting.infrastructure.persistence;

import com.github.cstettler.dddttc.accounting.domain.UserId;
import com.github.cstettler.dddttc.accounting.domain.Wallet;
import com.github.cstettler.dddttc.accounting.domain.WalletAlreadyExistsException;
import com.github.cstettler.dddttc.accounting.domain.WalletNotExistingException;
import com.github.cstettler.dddttc.accounting.domain.WalletRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.EmptySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Collection;

import static com.github.cstettler.dddttc.accounting.domain.UserId.userId;
import static com.github.cstettler.dddttc.accounting.domain.WalletBuilder.wallet;
import static com.github.cstettler.dddttc.accounting.domain.WalletMatcher.walletWith;
import static com.github.cstettler.dddttc.support.ReflectionBasedStateMatcher.hasState;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@JdbcTest
@ContextConfiguration(classes = JdbcWalletRepositoryTests.LocalConfiguration.class)
class JdbcWalletRepositoryTests {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Test
    void addAndGet_newWallet_storesWalletInDatabase() {
        // arrange
        Wallet wallet = wallet()
                .id("user-1")
                .build();

        // act
        this.walletRepository.add(wallet);
        Wallet loadedWallet = this.walletRepository.get(wallet.id());

        // assert
        assertThat(rowCountInDatabase("wallet"), is(1));

        assertThat(loadedWallet, hasState(walletWith()
                .id(is(userId("user-1")))
        ));
    }

    @Test
    void add_walletWithSameUserIdAlreadyExisting_throwsWalletAlreadyExistsException() {
        // arrange
        Wallet existingWallet = wallet()
                .id("user-1")
                .build();
        this.walletRepository.add(existingWallet);

        Wallet wallet = wallet()
                .id("user-1")
                .build();

        // act + assert
        assertThrows(WalletAlreadyExistsException.class, () -> this.walletRepository.add(wallet));
    }

    @Test
    void get_nonExistingWallet_throwsWalletNotExistingException() {
        // arrange
        UserId walletId = userId("non-existing-user");

        // act + assert
        assertThrows(WalletNotExistingException.class, () -> this.walletRepository.get(walletId));
    }

    @Test
    void findAll_existingWallets_returnsAllWallets() {
        // arrange
        this.walletRepository.add(wallet().build());
        this.walletRepository.add(wallet().build());
        this.walletRepository.add(wallet().build());

        // act
        Collection<Wallet> wallets = this.walletRepository.findAll();

        // assert
        assertThat(wallets, hasSize(3));
    }

    @Test
    void findAll_noExistingWallets_returnsEmptyList() {
        // act
        Collection<Wallet> wallets = this.walletRepository.findAll();

        // assert
        assertThat(wallets, hasSize(0));
    }

    @SuppressWarnings("ConstantConditions")
    private int rowCountInDatabase(String tableName) {
        return this.jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, EmptySqlParameterSource.INSTANCE, Integer.class);
    }


    static class LocalConfiguration {

        @Bean
        JdbcWalletRepository walletRepository(NamedParameterJdbcTemplate jdbcTemplate) {
            return new JdbcWalletRepository(jdbcTemplate);
        }

    }

}