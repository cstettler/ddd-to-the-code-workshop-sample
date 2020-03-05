package com.github.cstettler.dddttc.accounting.domain;

class TransactionWithSameReferenceAlreadyAppliedException extends RuntimeException {

    private TransactionWithSameReferenceAlreadyAppliedException(UserId walletId, TransactionReference transactionReference) {
        super("transaction with reference '" + transactionReference.value() + " has already been applied to wallet '" + walletId.value() + "'");
    }

    static TransactionWithSameReferenceAlreadyAppliedException transactionWithSameReferenceAlreadyApplied(UserId walletId, TransactionReference transactionReference) {
        return new TransactionWithSameReferenceAlreadyAppliedException(walletId, transactionReference);
    }

}
