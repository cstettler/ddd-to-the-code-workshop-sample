package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.DomainEvent;

@DomainEvent
class WalletInitializedEvent {

    private final WalletOwner walletOwner;

    private WalletInitializedEvent(WalletOwner walletOwner) {
        this.walletOwner = walletOwner;
    }

    WalletOwner walletOwner() {
        return this.walletOwner;
    }

    static WalletInitializedEvent walletInitialized(WalletOwner walletOwner) {
        return new WalletInitializedEvent(walletOwner);
    }

}
