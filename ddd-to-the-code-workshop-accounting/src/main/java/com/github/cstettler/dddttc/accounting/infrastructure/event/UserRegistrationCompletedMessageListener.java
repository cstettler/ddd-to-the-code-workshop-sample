package com.github.cstettler.dddttc.accounting.infrastructure.event;

import com.github.cstettler.dddttc.accounting.application.WalletService;
import com.github.cstettler.dddttc.accounting.domain.WalletAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static com.github.cstettler.dddttc.accounting.domain.WalletOwner.walletOwner;

@Component
class UserRegistrationCompletedMessageListener {

    private static Logger LOGGER = LoggerFactory.getLogger(UserRegistrationCompletedMessageListener.class);

    private final WalletService walletService;

    UserRegistrationCompletedMessageListener(WalletService walletService) {
        this.walletService = walletService;
    }

    @JmsListener(destination = "registration/user-registration-completed")
    public void onUserRegistrationCompleted(UserRegistrationCompletedMessage message) {
        String userHandle = message.userHandle.value;

        LOGGER.info("received user registration completed message for user '" + userHandle + "'");

        try {
            this.walletService.initializeWallet(walletOwner(userHandle));

            LOGGER.info("initialized wallet for user handle '" + userHandle + "'");
        } catch (WalletAlreadyExistsException e) {
            // ignored
        }
    }


    public static class UserRegistrationCompletedMessage {

        ValueWrapper userHandle;

    }


    public static class ValueWrapper {

        String value;

    }

}
