package com.github.cstettler.dddttc.accounting.domain;

class TransactionWithSameReferenceAlreadyAppliedException extends RuntimeException {

    private TransactionWithSameReferenceAlreadyAppliedException(WalletOwner walletOwner, TransactionReference transactionReference) {
        super("transaction with reference '" + transactionReference.value() + " has already been applied to wallet '" + walletOwner.value() + "'");
    }

    static TransactionWithSameReferenceAlreadyAppliedException transactionWithSameReferenceAlreadyApplied(WalletOwner walletOwner, TransactionReference transactionReference) {
        return new TransactionWithSameReferenceAlreadyAppliedException(walletOwner, transactionReference);
    }

}
