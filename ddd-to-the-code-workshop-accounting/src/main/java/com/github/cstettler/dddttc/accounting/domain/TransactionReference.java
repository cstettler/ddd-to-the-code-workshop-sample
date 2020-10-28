package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

@ValueObject
class TransactionReference {

    private final String value;

    private TransactionReference(String value) {
        this.value = value;
    }

    String value() {
        return this.value;
    }

    static TransactionReference transactionReference(String value) {
        return new TransactionReference(value);
    }

}
