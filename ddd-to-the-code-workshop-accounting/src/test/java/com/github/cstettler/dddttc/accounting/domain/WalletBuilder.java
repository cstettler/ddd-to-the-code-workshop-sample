package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.support.ReflectionBasedStateBuilder;

import static com.github.cstettler.dddttc.accounting.domain.UserId.userId;

@SuppressWarnings("UnusedReturnValue")
public class WalletBuilder extends ReflectionBasedStateBuilder<Wallet> {

    private WalletBuilder() {
        id(anyUserId());
    }

    public WalletBuilder id(String idValue) {
        return id(userId(idValue));
    }

    public WalletBuilder id(UserId id) {
        return recordProperty(this, "id", id);
    }

    public static WalletBuilder wallet() {
        return new WalletBuilder();
    }

    private static UserId anyUserId() {
        return UserId.userId(randomString(10));
    }

}
