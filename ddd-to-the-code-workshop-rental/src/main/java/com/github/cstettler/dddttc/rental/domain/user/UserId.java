package com.github.cstettler.dddttc.rental.domain.user;

import com.github.cstettler.dddttc.stereotype.ValueObject;

@ValueObject
public class UserId {

    private String value;

    private UserId(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static UserId userId(String value) {
        return new UserId(value);
    }

}
