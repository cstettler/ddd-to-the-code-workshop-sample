package com.github.cstettler.dddttc.registration.application;

import com.github.cstettler.dddttc.registration.domain.FullName;
import com.github.cstettler.dddttc.registration.domain.PhoneNumber;
import com.github.cstettler.dddttc.registration.domain.UserHandle;
import com.github.cstettler.dddttc.registration.domain.UserRegistration;
import com.github.cstettler.dddttc.registration.domain.UserRegistration.UserRegistrationFactory;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationId;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationRepository;
import com.github.cstettler.dddttc.registration.domain.VerificationCode;
import org.junit.jupiter.api.Test;

import static com.github.cstettler.dddttc.registration.domain.FullName.fullName;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumber.phoneNumber;
import static com.github.cstettler.dddttc.registration.domain.UserHandle.userHandle;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationBuilder.userRegistration;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationId.userRegistrationId;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationMatcher.userRegistrationWith;
import static com.github.cstettler.dddttc.registration.domain.VerificationCode.verificationCode;
import static com.github.cstettler.dddttc.support.ReflectionBasedStateMatcher.hasState;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

class UserRegistrationServiceTests {

    @Test
    void startNewUserRegistrationProcess_userHandleAndPhoneNumberProvided_addsNewUserRegistrationInRepositoryAndReturnsAggregateId() {
        // arrange
        UserRegistrationId userRegistrationId = userRegistrationId("user-registration-1");
        UserHandle userHandle = userHandle("peter");
        PhoneNumber phoneNumber = phoneNumber("+41 79 123 45 67");

        UserRegistration userRegistration = userRegistration()
                .id(userRegistrationId)
                .userHandle(userHandle)
                .phoneNumber(phoneNumber)
                .build();

        UserRegistrationRepository userRegistrationRepository = emptyUserRegistrationRepository();
        UserRegistrationService userRegistrationService = new UserRegistrationService(userRegistrationFactory(userRegistration), userRegistrationRepository);

        // act
        UserRegistrationId newUserRegistrationId = userRegistrationService.startNewUserRegistrationProcess(userHandle, phoneNumber);

        // assert
        assertThat(newUserRegistrationId, is(userRegistrationId));
        verify(userRegistrationRepository).add(userRegistration);
    }

    @Test
    void verifyPhoneNumber_existingUserRegistrationIdAndValidVerificationCodeProvided_verifiesPhoneNumberAndUpdatesUserRegistrationInRepository() {
        // arrange
        UserRegistrationId userRegistrationId = userRegistrationId("user-registration-1");
        VerificationCode verificationCode = verificationCode("123456");

        UserRegistration userRegistration = userRegistration()
                .id(userRegistrationId)
                .phoneNumberVerificationCode(verificationCode)
                .build();

        UserRegistrationRepository userRegistrationRepository = userRegistrationRepositoryWith(userRegistration);
        UserRegistrationService userRegistrationService = new UserRegistrationService(null, userRegistrationRepository);

        // act
        userRegistrationService.verifyPhoneNumber(userRegistrationId, verificationCode);

        // assert
        verify(userRegistrationRepository).update(argThat(hasState(userRegistrationWith()
                .id(is(userRegistrationId))
                .phoneNumberVerified(is(true))
        )));
    }

    @Test
    void completeUserRegistration_existingUserRegistrationIdAndFullNameProvided_completesUserRegistrationUpdatesUserRegistration() {
        // arrange
        UserRegistrationId userRegistrationId = userRegistrationId("user-registration-1");
        UserHandle userHandle = userHandle("peter");
        PhoneNumber phoneNumber = phoneNumber("+41 79 123 45 67");
        FullName fullName = fullName("Peter", "Smith");

        UserRegistration userRegistration = userRegistration()
                .id(userRegistrationId)
                .userHandle(userHandle)
                .phoneNumber(phoneNumber)
                .phoneNumberVerified(true)
                .build();

        UserRegistrationRepository userRegistrationRepository = userRegistrationRepositoryWith(userRegistration);
        UserRegistrationService userRegistrationService = new UserRegistrationService(null, userRegistrationRepository);

        // act
        userRegistrationService.completeUserRegistration(userRegistrationId, fullName);

        // assert
        verify(userRegistrationRepository).update(argThat(hasState(userRegistrationWith()
                .id(is(userRegistrationId))
                .completed(is(true))
        )));
    }

    private static UserRegistrationFactory userRegistrationFactory(UserRegistration userRegistration) {
        UserRegistrationFactory userRegistrationFactory = mock(UserRegistrationFactory.class);
        when(userRegistrationFactory.newUserRegistration(userRegistration.userHandle(), userRegistration.phoneNumber())).thenReturn(userRegistration);

        return userRegistrationFactory;
    }

    private static UserRegistrationRepository emptyUserRegistrationRepository() {
        return mock(UserRegistrationRepository.class);
    }

    private static UserRegistrationRepository userRegistrationRepositoryWith(UserRegistration userRegistration) {
        UserRegistrationRepository userRegistrationRepository = mock(UserRegistrationRepository.class);
        when(userRegistrationRepository.get(userRegistration.id())).thenReturn(userRegistration);

        return userRegistrationRepository;
    }

}