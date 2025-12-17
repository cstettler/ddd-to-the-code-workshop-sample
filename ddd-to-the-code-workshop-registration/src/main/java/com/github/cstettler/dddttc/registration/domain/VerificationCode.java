package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.ValueObject;

import static java.lang.String.valueOf;
import static java.util.stream.Collectors.joining;
import static java.util.stream.IntStream.range;

@ValueObject
public class VerificationCode {

    private final String value;

    private VerificationCode() {
        this(pseudoRandomNumericString(6));
    }

    private VerificationCode(String value) {
        this.value = value;
    }

    String value() {
        return this.value;
    }

    boolean matches(VerificationCode verificationCode) {
        return this.value.equals(verificationCode.value());
    }

    public static VerificationCode verificationCode(String value) {
        return new VerificationCode(value);
    }

    static VerificationCode randomVerificationCode() {
        return new VerificationCode();
    }

    private static String pseudoRandomNumericString(int length) {
        return range(0, length)
                .map((i) -> (int) (Math.random() * 10 % 10))
                .mapToObj((number) -> valueOf(number))
                .collect(joining());
    }

}
