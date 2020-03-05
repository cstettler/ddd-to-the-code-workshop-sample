package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class WalletAlreadyExistsException extends RuntimeException {

    private WalletAlreadyExistsException(UserId userId) {
        super("wallet '" + userId.value() + "' already exists");
    }

    public static WalletAlreadyExistsException walletAlreadyExists(UserId userId) {
        return new WalletAlreadyExistsException(userId);
    }

}
