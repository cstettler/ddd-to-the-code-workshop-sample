package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

@ValueObject
class Transaction {

    private final TransactionReference reference;
    private final Amount amount;

    private Transaction(TransactionReference reference, Amount amount) {
        this.reference = reference;
        this.amount = amount;
    }

    TransactionReference reference() {
        return this.reference;
    }

    Amount amount() {
        return this.amount;
    }

    static Transaction transaction(TransactionReference transactionReference, Amount amount) {
        return new Transaction(transactionReference, amount);
    }

}
