package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.DomainEvent;

@DomainEvent
class WalletInitializedEvent {

    private final UserId walletId;

    private WalletInitializedEvent(UserId walletId) {
        this.walletId = walletId;
    }

    UserId walletId() {
        return this.walletId;
    }

    static WalletInitializedEvent walletInitialized(UserId walletId) {
        return new WalletInitializedEvent(walletId);
    }

}
