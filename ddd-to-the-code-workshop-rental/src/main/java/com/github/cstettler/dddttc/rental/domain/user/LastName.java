package com.github.cstettler.dddttc.rental.domain.user;

import com.github.cstettler.dddttc.stereotype.ValueObject;

@ValueObject
public class LastName {

    private final String value;

    private LastName(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static LastName lastName(String value) {
        return new LastName(value);
    }

}
