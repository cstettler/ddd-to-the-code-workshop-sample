package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

@ValueObject
public class PhoneNumber {

    private final String value;

    private PhoneNumber(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    boolean isSwiss() {
        return this.value != null && (this.value.startsWith("+41") || this.value.startsWith("0041"));
    }

    public static PhoneNumber phoneNumber(String value) {
        return new PhoneNumber(value);
    }

}
