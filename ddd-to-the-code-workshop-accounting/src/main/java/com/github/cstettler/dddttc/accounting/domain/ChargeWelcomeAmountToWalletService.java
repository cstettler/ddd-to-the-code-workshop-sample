package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.DomainEventHandler;
import com.github.cstettler.dddttc.stereotype.DomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

import static com.github.cstettler.dddttc.accounting.domain.Amount.amount;
import static com.github.cstettler.dddttc.accounting.domain.TransactionReference.transactionReference;

@DomainService
class ChargeWelcomeAmountToWalletService {

    private static Logger LOGGER = LoggerFactory.getLogger(ChargeWelcomeAmountToWalletService.class);

    private final WalletRepository walletRepository;
    private final Amount welcomeAmount;

    ChargeWelcomeAmountToWalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
        this.welcomeAmount = amount(new BigDecimal("5.00"));
    }

    @DomainEventHandler
    void chargeWelcomeAmountToWallet(WalletInitializedEvent event) {
        try {
            WalletOwner walletOwner = event.walletOwner();
            TransactionReference welcomeAmountTransactionReference = transactionReference("welcome-" + walletOwner.value());

            Wallet wallet = this.walletRepository.get(walletOwner);
            wallet.chargeAmount(this.welcomeAmount, welcomeAmountTransactionReference);

            this.walletRepository.update(wallet);

            LOGGER.info("charged welcome amount of '" + this.welcomeAmount.value() + "' to wallet of owner '" + walletOwner.value() + "'");
        } catch (TransactionWithSameReferenceAlreadyAppliedException e) {
            // ignored
        }
    }

}
