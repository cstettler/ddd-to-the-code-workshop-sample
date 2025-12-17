package com.github.cstettler.dddttc.registration.domain;

import org.junit.jupiter.api.Test;

import static com.github.cstettler.dddttc.registration.domain.UserHandle.userHandle;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class UserHandleTests {

    @Test
    void equals_userHandlesWithSameValues_returnsTrue() {
        // arrange
        UserHandle userHandleOne = userHandle("peter");
        UserHandle userHandleTwo = userHandle("peter");

        // act + assert
        assertThat(userHandleOne.equals(userHandleTwo), is(true));
        assertThat(userHandleTwo.equals(userHandleOne), is(true));
    }

}