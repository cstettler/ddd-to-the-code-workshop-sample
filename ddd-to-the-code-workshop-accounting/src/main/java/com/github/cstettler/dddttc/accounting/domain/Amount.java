package com.github.cstettler.dddttc.accounting.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

import java.math.BigDecimal;

import static java.math.BigDecimal.ZERO;

@ValueObject
public class Amount {

    private final BigDecimal value;

    private Amount(BigDecimal value) {
        this.value = value;
    }

    public BigDecimal value() {
        return this.value;
    }

    Amount negate() {
        return amount(this.value.negate());
    }

    Amount add(Amount other) {
        return amount(this.value.add(other.value));
    }

    static Amount zero() {
        return amount(ZERO);
    }

    static Amount amount(BigDecimal value) {
        return new Amount(value);
    }
}
