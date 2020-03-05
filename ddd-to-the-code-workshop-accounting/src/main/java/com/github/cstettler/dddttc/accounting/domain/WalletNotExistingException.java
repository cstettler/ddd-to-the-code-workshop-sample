package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class WalletNotExistingException extends RuntimeException {

    private WalletNotExistingException(UserId userId) {
        super("wallet '" + userId.value() + "' does not exist");
    }

    public static WalletNotExistingException walletNotExisting(UserId userId) {
        return new WalletNotExistingException(userId);
    }

}
