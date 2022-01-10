package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.Repository;

import java.util.Collection;

@Repository
public interface WalletRepository {

    void add(Wallet wallet) throws WalletAlreadyExistsException;

    void update(Wallet wallet) throws WalletNotExistingException;

    Wallet get(WalletOwner walletOwner) throws WalletNotExistingException;

    Collection<Wallet> findAll();

}
