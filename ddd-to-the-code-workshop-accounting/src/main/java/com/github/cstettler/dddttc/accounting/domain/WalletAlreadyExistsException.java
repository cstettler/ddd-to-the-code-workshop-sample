package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class WalletAlreadyExistsException extends RuntimeException {

    private WalletAlreadyExistsException(WalletOwner walletOwner) {
        super("wallet '" + walletOwner.value() + "' already exists");
    }

    public static WalletAlreadyExistsException walletAlreadyExists(WalletOwner walletOwner) {
        return new WalletAlreadyExistsException(walletOwner);
    }

}
