package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.support.ReflectionBasedStateBuilder;

@SuppressWarnings("UnusedReturnValue")
public class WalletBuilder extends ReflectionBasedStateBuilder<Wallet> {

    private WalletBuilder() {
        walletOwner(anyWalletOwner());
    }

    public WalletBuilder walletOwner(String idValue) {
        return walletOwner(WalletOwner.walletOwner(idValue));
    }

    public WalletBuilder walletOwner(WalletOwner walletOwner) {
        return recordProperty(this, "walletOwner", walletOwner);
    }

    public static WalletBuilder wallet() {
        return new WalletBuilder();
    }

    private static WalletOwner anyWalletOwner() {
        return WalletOwner.walletOwner(randomString(10));
    }

}
