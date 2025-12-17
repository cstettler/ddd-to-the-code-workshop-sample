package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.registration.domain.UserRegistration.UserRegistrationFactory;
import com.github.cstettler.dddttc.registration.domain.support.MandatoryParameterMissingException;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;
import com.github.cstettler.dddttc.support.domain.RecordingDomainEventPublisher;
import org.junit.jupiter.api.Test;

import static com.github.cstettler.dddttc.registration.domain.FullName.fullName;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumber.phoneNumber;
import static com.github.cstettler.dddttc.registration.domain.UserHandle.userHandle;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationBuilder.anyPhoneNumber;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationBuilder.anyUserHandle;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationBuilder.userRegistration;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationCompletedEventMatcher.userRegistrationCompletedEventWith;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationMatcher.userRegistrationWith;
import static com.github.cstettler.dddttc.registration.domain.VerificationCode.verificationCode;
import static com.github.cstettler.dddttc.support.ReflectionBasedStateMatcher.hasState;
import static com.github.cstettler.dddttc.support.ReflectionUtils.propertyValue;
import static com.github.cstettler.dddttc.support.domain.RecordingDomainEventPublisher.recordingDomainEventPublisher;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

class UserRegistrationTests {

    @Test
    void newUserRegistration_mandatoryParametersProvided_createsNewUserRegistrationForUserHandleAndPhoneNumber() {
        // arrange
        UserHandle userHandle = userHandle("peter");
        PhoneNumber phoneNumber = phoneNumber("+41 79 123 45 67");

        UserRegistrationFactory userRegistrationFactory = new UserRegistrationFactory(emptyUserRegistrationRepository(), recordingDomainEventPublisher());

        // act
        UserRegistration userRegistration = userRegistrationFactory.newUserRegistration(userHandle, phoneNumber);

        // assert
        assertThat(userRegistration, hasState(userRegistrationWith()
                .id(is(not(nullValue())))
                .userHandle(is(userHandle))
                .phoneNumber(is(phoneNumber))
        ));
    }

    @Test
    void newUserRegistration_mandatoryParametersProvided_generatesPhoneNumberVerificationCode() {
        // arrange
        UserHandle userHandle = userHandle("peter");
        PhoneNumber phoneNumber = phoneNumber("+41 79 123 45 67");

        UserRegistrationFactory userRegistrationFactory = new UserRegistrationFactory(emptyUserRegistrationRepository(), recordingDomainEventPublisher());

        // act
        UserRegistration userRegistration = userRegistrationFactory.newUserRegistration(userHandle, phoneNumber);

        // assert
        assertThat(userRegistration, hasState(userRegistrationWith()
                .phoneNumberVerificationCode(is(not(nullValue())))
                .phoneNumberVerified(is(false))
        ));
    }

    @Test
    void newUserRegistration_mandatoryParametersProvided_publishesPhoneNumberVerificationCodeGeneratedEvent() {
        // arrange
        UserHandle userHandle = userHandle("peter");
        PhoneNumber phoneNumber = phoneNumber("+41 79 123 45 67");

        RecordingDomainEventPublisher recordingDomainEventPublisher = recordingDomainEventPublisher();
        UserRegistrationFactory userRegistrationFactory = new UserRegistrationFactory(emptyUserRegistrationRepository(), recordingDomainEventPublisher);

        // act
        UserRegistration userRegistration = userRegistrationFactory.newUserRegistration(userHandle, phoneNumber);

        // assert
        assertThat(recordingDomainEventPublisher.numberOfRecordedDomainEvents(), is(1));

        PhoneNumberVerificationCodeGeneratedEvent event = recordingDomainEventPublisher.singleRecordedDomainEvent();
        assertThat(event.phoneNumber(), is(phoneNumber));
        assertThat(event.verificationCode(), is(phoneNumberVerificationCodeFrom(userRegistration)));
    }

    @Test
    void newUserRegistration_userHandleNotProvided_throwsMandatoryParameterMissingException() {
        // arrange
        UserHandle userHandle = null;
        PhoneNumber phoneNumber = anyPhoneNumber();

        UserRegistrationFactory userRegistrationFactory = new UserRegistrationFactory(emptyUserRegistrationRepository(), recordingDomainEventPublisher());

        // act + assert
        assertThrows(MandatoryParameterMissingException.class, () -> userRegistrationFactory.newUserRegistration(userHandle, phoneNumber), "mandatory parameter 'userHandle' is missing");
    }

    @Test
    void newUserRegistration_phoneNumberNotProvided_throwsMandatoryParameterMissingException() {
        // arrange
        UserHandle userHandle = anyUserHandle();
        PhoneNumber phoneNumber = null;

        UserRegistrationFactory userRegistrationFactory = new UserRegistrationFactory(emptyUserRegistrationRepository(), recordingDomainEventPublisher());

        // act + assert
        assertThrows(MandatoryParameterMissingException.class, () -> userRegistrationFactory.newUserRegistration(userHandle, phoneNumber), "mandatory parameter 'phoneNumber' is missing");
    }

    @Test
    void newUserRegistration_existingUserRegistrationWithSameUserHandle_throwsUserHandleAlreadyInUseException() {
        // arrange
        UserHandle userHandle = userHandle("peter");
        PhoneNumber phoneNumber = phoneNumber("+41 79 123 45 67");

        UserRegistration existingUserRegistration = userRegistration()
                .userHandle(userHandle)
                .build();

        UserRegistrationFactory userRegistrationFactory = new UserRegistrationFactory(userRegistrationRepositoryWith(existingUserRegistration), recordingDomainEventPublisher());

        // act
        assertThrows(UserHandleAlreadyInUseException.class, () -> userRegistrationFactory.newUserRegistration(userHandle, phoneNumber));

        // assert
        verify(userRegistrationRepositoryWith(existingUserRegistration), never()).add(any());
    }

    @Test
    void verifyPhoneNumber_validVerificationCodeProvided_marksPhoneNumberAsVerified() {
        // arrange
        UserRegistration userRegistration = userRegistration()
                .phoneNumberVerificationCode(verificationCode("123456"))
                .phoneNumberVerified(false)
                .build();

        // act
        userRegistration.verifyPhoneNumber(verificationCode("123456"));

        // assert
        assertThat(userRegistration, hasState(userRegistrationWith()
                .phoneNumberVerified(is(true))
        ));
    }

    @Test
    void verifyPhoneNumber_verificationCodeNotProvided_throwsMandatoryParameterMissingExceptionAndDoesNotMarkPhoneNumberAsVerified() {
        // arrange
        UserRegistration userRegistration = userRegistration().build();

        // act + assert
        assertThrows(MandatoryParameterMissingException.class, () -> userRegistration.verifyPhoneNumber(null), "mandatory parameter 'verificationCode' is missing");
        assertThat(userRegistration, hasState(userRegistrationWith()
                .phoneNumberVerified(is(false))
        ));
    }

    @Test
    void verifyPhoneNumber_invalidVerificationCodeProvided_throwsPhoneNumberVerificationCodeInvalidExceptionAndDoesNotMarkPhoneNumberAsVerified() {
        // arrange
        UserRegistration userRegistration = userRegistration()
                .phoneNumberVerificationCode(verificationCode("123456"))
                .phoneNumberVerified(false)
                .build();

        VerificationCode invalidPhoneNumberVerificationCode = verificationCode("999999");

        // act + assert
        assertThrows(PhoneNumberVerificationCodeInvalidException.class, () -> userRegistration.verifyPhoneNumber(invalidPhoneNumberVerificationCode), "phone number verification code '999999' is invalid");
        assertThat(userRegistration, hasState(userRegistrationWith()
                .phoneNumberVerified(is(false))
        ));
    }

    @Test
    void complete_userRegistrationWithVerifiedPhoneNumberAndFullNameProvided_marksUserRegistrationAsCompletedAndPublishesUserRegistrationCompletedEvent() {
        // arrange
        DomainEventPublisher domainEventPublisher = domainEventPublisher();
        UserHandle userHandle = userHandle("peter");
        PhoneNumber phoneNumber = phoneNumber("+41 79 123 45 67");

        UserRegistration userRegistration = userRegistration()
                .domainEventPublisher(domainEventPublisher)
                .userHandle(userHandle)
                .phoneNumber(phoneNumber)
                .phoneNumberVerified(true)
                .build();

        FullName fullName = fullName("Peter", "Smith");

        // act
        userRegistration.complete(fullName);

        // assert
        assertThat(userRegistration, hasState(userRegistrationWith()
                .fullName(is(fullName))
                .completed(is(true))
        ));

        verify(domainEventPublisher).publish(argThat(hasState(userRegistrationCompletedEventWith()
                .userHandle(is(userHandle))
                .phoneNumber(is(phoneNumber))
                .fullName(is(fullName))
        )));
    }

    @Test
    void complete_fullNameNotProvided_throwsMandatoryParameterMissingExceptionAndDoesNotMarkUserRegistrationAsCompleted() {
        // arrange
        UserRegistration userRegistration = userRegistration()
                .phoneNumberVerified(true)
                .build();

        FullName fullName = null;

        // act
        assertThrows(MandatoryParameterMissingException.class, () -> userRegistration.complete(fullName), "mandatory parameter 'fullName' is missing");
        assertThat(userRegistration, hasState(userRegistrationWith()
                .fullName(is(fullName))
                .completed(is(false))
        ));
    }

    @Test
    void complete_userRegistrationWithoutVerifiedPhoneNumberAndFullNameProvided_throwsPhoneNumberNotYetVerifiedExceptionAndDoesNotMarkUserRegistrationAsCompleted() {
        // arrange
        UserRegistration userRegistration = userRegistration()
                .phoneNumber(phoneNumber("+41 79 123 45 67"))
                .phoneNumberVerified(false)
                .completed(false)
                .build();

        FullName fullName = fullName("Peter", "Smith");

        // act + assert
        assertThrows(PhoneNumberNotYetVerifiedException.class, () -> userRegistration.complete(fullName), "phone number '+41 79 123 45 67' has not yet been verified");
        assertThat(userRegistration, hasState(userRegistrationWith()
                .fullName(is(nullValue()))
                .completed(is(false))
        ));
    }

    @Test
    void complete_alreadyCompletedUserRegistration_throwsUserRegistrationAlreadyCompletedException() {
        // arrange
        UserRegistration userRegistration = userRegistration()
                .id("user-registration-1")
                .phoneNumberVerified(true)
                .completed(true)
                .build();

        FullName fullName = fullName("Peter", "Smith");

        // act + assert
        assertThrows(UserRegistrationAlreadyCompletedException.class, () -> userRegistration.complete(fullName), "user registration 'user-registration-1' has already been completed");
    }

    private static UserRegistrationRepository emptyUserRegistrationRepository() {
        return mock(UserRegistrationRepository.class);
    }

    private static UserRegistrationRepository userRegistrationRepositoryWith(UserRegistration userRegistration) {
        UserRegistrationRepository userRegistrationRepository = mock(UserRegistrationRepository.class);
        when(userRegistrationRepository.find(userRegistration.userHandle())).thenReturn(userRegistration);

        return userRegistrationRepository;
    }

    private static DomainEventPublisher domainEventPublisher() {
        return mock(DomainEventPublisher.class);
    }

    private static VerificationCode phoneNumberVerificationCodeFrom(UserRegistration userRegistration) {
        return propertyValue(userRegistration, "phoneNumberVerificationCode");
    }

}