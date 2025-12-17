package com.github.cstettler.dddttc.rental.domain.user;

import com.github.cstettler.dddttc.stereotype.ValueObject;

@ValueObject
public class FirstName {

    private final String value;

    private FirstName(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static FirstName firstName(String value) {
        return new FirstName(value);
    }

}
