package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

@ValueObject
public class UserHandle {

    private final String value;

    private UserHandle(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static UserHandle userHandle(String value) {
        return new UserHandle(value);
    }

}
