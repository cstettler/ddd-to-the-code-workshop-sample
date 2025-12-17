package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

@ValueObject
public class FullName {

    private final String firstName;
    private final String lastName;

    private FullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String firstAndLastName() {
        return this.firstName + " " + this.lastName;
    }

    public static FullName fullName(String firstName, String lastName) {
        return new FullName(firstName, lastName);
    }

}
