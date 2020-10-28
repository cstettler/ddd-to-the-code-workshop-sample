package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

import static java.util.UUID.randomUUID;

@ValueObject
public class UserRegistrationId {

    private final String value;

    private UserRegistrationId() {
        this(randomUUID().toString());
    }

    private UserRegistrationId(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static UserRegistrationId userRegistrationId(String value) {
        return new UserRegistrationId(value);
    }

    static UserRegistrationId newUserRegistrationId() {
        return new UserRegistrationId();
    }

}
