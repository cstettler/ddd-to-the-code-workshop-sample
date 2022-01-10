package com.github.cstettler.dddttc.registration.domain;

import org.junit.jupiter.api.Test;

import static com.github.cstettler.dddttc.registration.domain.PhoneNumber.phoneNumber;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class PhoneNumberTests {

    @Test
    void isSwiss_phoneNumberWithPlusFourtyOnePrefix_returnsTrue() {
        // arrange
        PhoneNumber phoneNumber = phoneNumber("+4179123456");

        // act
        boolean swiss = phoneNumber.isSwiss();

        // assert
        assertThat(swiss, is(true));
    }

    @Test
    void isSwiss_phoneNumberWithZeroFourtyOnePrefix_returnsTrue() {
        // arrange
        PhoneNumber phoneNumber = phoneNumber("004179123456");

        // act
        boolean swiss = phoneNumber.isSwiss();

        // assert
        assertThat(swiss, is(true));
    }

    @Test
    void isSwiss_phoneNumberWithAnyOtherPrefix_returnsFalse() {
        // arrange
        PhoneNumber phoneNumber = phoneNumber("+42791234567");

        // act
        boolean swiss = phoneNumber.isSwiss();

        // assert
        assertThat(swiss, is(false));
    }

    @Test
    void isSwiss_phoneNumberWithNullValue_returnsFalse() {
        // arrange
        PhoneNumber phoneNumber = phoneNumber(null);

        // act
        boolean swiss = phoneNumber.isSwiss();

        // assert
        assertThat(swiss, is(false));
    }

}