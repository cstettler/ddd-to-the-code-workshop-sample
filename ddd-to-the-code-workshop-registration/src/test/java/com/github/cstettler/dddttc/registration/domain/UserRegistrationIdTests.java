package com.github.cstettler.dddttc.registration.domain;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.github.cstettler.dddttc.registration.domain.UserRegistrationId.newUserRegistrationId;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationId.userRegistrationId;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

class UserRegistrationIdTests {

    @Test
    void newUserRegistrationId_anyState_createsNewUserRegistrationIdWithInternalValue() {
        // act
        UserRegistrationId userRegistrationId = newUserRegistrationId();

        // assert
        assertThat(userRegistrationId, is(not(nullValue())));
        assertThat(userRegistrationId.value(), is(not(nullValue())));
    }

    @Test
    void newUserRegistrationId_multipleInvocations_generatesUniqueUserRegistrationIdValues() {
        // arrange
        int numberOfCreatedUserRegistrationIds = 10000;

        // act
        Set<String> uniqueUserRegistrationIdValues = range(0, numberOfCreatedUserRegistrationIds)
                .mapToObj((index) -> newUserRegistrationId())
                .map((userRegistrationId) -> userRegistrationId.value())
                .collect(toSet());

        // assert
        assertThat(uniqueUserRegistrationIdValues.size(), is(numberOfCreatedUserRegistrationIds));
    }

    @Test
    void userRegistrationId_valueProvided_inflatesUserRegistrationIdWithProvidedValue() {
        // act
        UserRegistrationId userRegistration = userRegistrationId("id-value");

        // assert
        assertThat(userRegistration, is(not(nullValue())));
        assertThat(userRegistration.value(), is(equalTo("id-value")));
    }

}