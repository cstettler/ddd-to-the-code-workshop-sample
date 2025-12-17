package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

@ValueObject
public class WalletOwner {

    private final String value;

    private WalletOwner(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static WalletOwner walletOwner(String value) {
        return new WalletOwner(value);
    }

}
