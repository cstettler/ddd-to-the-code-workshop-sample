package com.github.cstettler.dddttc.registration.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.github.cstettler.dddttc.registration.domain.VerificationCode.randomVerificationCode;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

class VerificationCodeTests {

    @Test
    void randomVerificationCode_anyState_returnsSixDigitsNumericString() {
        // act
        VerificationCode verificationCode = randomVerificationCode();

        // assert
        assertThat(verificationCode.value().length(), is(6));
    }

    @Test
    void randomVerificationCode_multipleInvocations_generatesNearlyUniqueVerificationCodes() {
        // arrange
        int numberOfGeneratedVerificationCodes = 10000;

        // act
        Set<String> uniqueVerificationCodeValues = range(0, numberOfGeneratedVerificationCodes)
                .mapToObj((index) -> randomVerificationCode())
                .map((verificationCode) -> verificationCode.value())
                .collect(toSet());

        // assert
        assertThat(uniqueVerificationCodeValues.size(), greaterThan(numberOfGeneratedVerificationCodes - 100));
    }

}