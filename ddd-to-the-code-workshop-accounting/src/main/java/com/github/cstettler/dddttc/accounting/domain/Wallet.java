package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.Aggregate;
import com.github.cstettler.dddttc.stereotype.AggregateFactory;
import com.github.cstettler.dddttc.stereotype.AggregateId;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;

import java.util.ArrayList;
import java.util.List;

import static com.github.cstettler.dddttc.accounting.domain.Amount.zero;
import static com.github.cstettler.dddttc.accounting.domain.BookingAlreadyBilledException.bookingAlreadyBilled;
import static com.github.cstettler.dddttc.accounting.domain.Transaction.transaction;
import static com.github.cstettler.dddttc.accounting.domain.TransactionReference.transactionReference;
import static com.github.cstettler.dddttc.accounting.domain.TransactionWithSameReferenceAlreadyAppliedException.transactionWithSameReferenceAlreadyApplied;
import static com.github.cstettler.dddttc.accounting.domain.WalletInitializedEvent.walletInitialized;

@Aggregate
public class Wallet {

    private final WalletOwner walletOwner;
    private final List<Transaction> transactions;
    private Amount balance;

    private Wallet(WalletOwner walletOwner, DomainEventPublisher domainEventPublisher) {
        this.walletOwner = walletOwner;
        this.transactions = new ArrayList<>();
        this.balance = zero();

        domainEventPublisher.publish(walletInitialized(this.walletOwner));
    }

    @AggregateId
    public WalletOwner walletOwner() {
        return this.walletOwner;
    }

    public Amount balance() {
        return this.balance;
    }

    void chargeAmount(Amount amount, TransactionReference transactionReference) throws TransactionWithSameReferenceAlreadyAppliedException {
        applyTransaction(transaction(transactionReference, amount));
    }

    public void billBookingFee(Booking booking, BookingFeePolicy bookingFeePolicy) throws BookingAlreadyBilledException {
        try {
            Amount bookingFee = bookingFeePolicy.feeForBooking(booking);
            TransactionReference transactionReference = transactionReference(booking.id().value());

            applyTransaction(transaction(transactionReference, bookingFee.negate()));
        } catch (TransactionWithSameReferenceAlreadyAppliedException e) {
            throw bookingAlreadyBilled(this, booking);
        }
    }

    @AggregateFactory(Wallet.class)
    public static Wallet newWallet(WalletOwner walletOwner, DomainEventPublisher domainEventPublisher) {
        return new Wallet(walletOwner, domainEventPublisher);
    }

    private Amount recalculateBalance() {
        return this.transactions.stream()
                .map((transaction) -> transaction.amount())
                .reduce(zero(), (sum, amount) -> sum.add(amount));
    }

    private void applyTransaction(Transaction transaction) {
        if (hasAlreadyBeenApplied(transaction)) {
            throw transactionWithSameReferenceAlreadyApplied(this.walletOwner, transaction.reference());
        }

        this.transactions.add(transaction);
        this.balance = recalculateBalance();
    }

    private boolean hasAlreadyBeenApplied(Transaction transaction) {
        return this.transactions.stream()
                .anyMatch((existingTransaction) -> existingTransaction.reference().equals(transaction.reference()));
    }

}

