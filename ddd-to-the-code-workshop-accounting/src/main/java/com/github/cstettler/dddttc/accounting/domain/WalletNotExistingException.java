package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class WalletNotExistingException extends RuntimeException {

    private WalletNotExistingException(WalletOwner walletOwner) {
        super("wallet '" + walletOwner.value() + "' does not exist");
    }

    public static WalletNotExistingException walletNotExisting(WalletOwner walletOwner) {
        return new WalletNotExistingException(walletOwner);
    }

}
