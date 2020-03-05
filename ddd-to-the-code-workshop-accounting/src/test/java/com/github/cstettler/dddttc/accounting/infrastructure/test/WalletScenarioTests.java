package com.github.cstettler.dddttc.accounting.infrastructure.test;

import com.github.cstettler.dddttc.accounting.application.WalletService;
import com.github.cstettler.dddttc.accounting.domain.UserId;
import com.github.cstettler.dddttc.accounting.domain.Wallet;
import com.github.cstettler.dddttc.accounting.domain.WalletAlreadyExistsException;
import com.github.cstettler.dddttc.accounting.domain.WalletRepository;
import com.github.cstettler.dddttc.support.test.ScenarioTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;

import static com.github.cstettler.dddttc.accounting.domain.UserId.userId;
import static com.github.cstettler.dddttc.accounting.domain.WalletBuilder.wallet;
import static com.github.cstettler.dddttc.accounting.domain.WalletMatcher.walletWith;
import static com.github.cstettler.dddttc.support.ReflectionBasedStateMatcher.hasState;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ScenarioTest
class WalletScenarioTests {

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void initializeWallet_nonExistingWallet_createsNewWallet() {
        // arrange
        UserId userId = userId("peter");

        // act
        this.walletService.initializeWallet(userId);

        // assert
        invokeInTransaction(() -> {
            Wallet wallet = this.walletRepository.get(userId);

            assertThat(wallet, hasState(walletWith().id(is(userId))));
        });
    }

    @Test
    void initializeWallet_existingWallet_throwsWalletAlreadyExistsException() {
        // arrange
        UserId userId = userId("peter");

        invokeInTransaction(() -> this.walletRepository.add(wallet().id(userId).build()));

        // act + assert
        assertThrows(WalletAlreadyExistsException.class, () -> this.walletService.initializeWallet(userId));
    }

    @Test
    void listWallets() {
        // arrange
        invokeInTransaction(() -> {
            this.walletRepository.add(wallet().id("peter").build());
            this.walletRepository.add(wallet().id("hans").build());
        });

        // act
        Collection<Wallet> wallets = this.walletService.listWallets();

        assertThat(wallets, hasSize(2));
        assertThat(wallets, hasItem(hasState(walletWith().id(is(userId("peter"))))));
        assertThat(wallets, hasItem(hasState(walletWith().id(is(userId("hans"))))));
    }

    private void invokeInTransaction(Executable executable) {
        new TransactionTemplate(this.transactionManager).execute((status) -> {
            try {
                executable.execute();

                return null;
            } catch (Throwable t) {
                throw new IllegalStateException("unable to execute in transaction", t);
            }
        });
    }

}
