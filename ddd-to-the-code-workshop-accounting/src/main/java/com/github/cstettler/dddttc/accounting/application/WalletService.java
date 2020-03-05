package com.github.cstettler.dddttc.accounting.application;

import com.github.cstettler.dddttc.accounting.domain.Booking;
import com.github.cstettler.dddttc.accounting.domain.BookingAlreadyBilledException;
import com.github.cstettler.dddttc.accounting.domain.BookingFeePolicy;
import com.github.cstettler.dddttc.accounting.domain.UserId;
import com.github.cstettler.dddttc.accounting.domain.Wallet;
import com.github.cstettler.dddttc.accounting.domain.WalletAlreadyExistsException;
import com.github.cstettler.dddttc.accounting.domain.WalletRepository;
import com.github.cstettler.dddttc.stereotype.ApplicationService;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;

import java.util.Collection;

import static com.github.cstettler.dddttc.accounting.domain.Wallet.newWallet;

@ApplicationService
public class WalletService {

    private final BookingFeePolicy bookingFeePolicy;
    private final WalletRepository walletRepository;
    private final DomainEventPublisher domainEventPublisher;

    WalletService(BookingFeePolicy bookingFeePolicy, WalletRepository walletRepository, DomainEventPublisher domainEventPublisher) {
        this.bookingFeePolicy = bookingFeePolicy;
        this.walletRepository = walletRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    public void initializeWallet(UserId userId) throws WalletAlreadyExistsException {
        Wallet wallet = newWallet(userId, this.domainEventPublisher);

        this.walletRepository.add(wallet);
    }

    public void billBookingFee(Booking booking) throws BookingAlreadyBilledException {
        Wallet wallet = this.walletRepository.get(booking.userId());
        wallet.billBookingFee(booking, this.bookingFeePolicy);

        this.walletRepository.update(wallet);
    }

    public Collection<Wallet> listWallets() {
        return this.walletRepository.findAll();
    }

}
