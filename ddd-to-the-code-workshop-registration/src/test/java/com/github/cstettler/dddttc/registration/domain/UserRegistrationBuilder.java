package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.support.ReflectionBasedStateBuilder;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;

import static com.github.cstettler.dddttc.registration.domain.UserRegistrationId.userRegistrationId;
import static com.github.cstettler.dddttc.registration.domain.VerificationCode.randomVerificationCode;
import static org.mockito.Mockito.mock;

@SuppressWarnings("UnusedReturnValue")
public class UserRegistrationBuilder extends ReflectionBasedStateBuilder<UserRegistration> {

    private UserRegistrationBuilder() {
        domainEventPublisher(anyDomainEventPublisher());
        userHandle(anyUserHandle());
        phoneNumber(anyPhoneNumber());
        phoneNumberVerificationCode(anyPhoneNumberVerificationCode());
    }

    public UserRegistrationBuilder domainEventPublisher(DomainEventPublisher domainEventPublisher) {
        return recordProperty(this, "domainEventPublisher", domainEventPublisher);
    }

    public UserRegistrationBuilder id(String idValue) {
        return id(userRegistrationId(idValue));
    }

    public UserRegistrationBuilder id(UserRegistrationId id) {
        return recordProperty(this, "id", id);
    }

    public UserRegistrationBuilder userHandle(String userHandleValue) {
        return userHandle(UserHandle.userHandle(userHandleValue));
    }

    public UserRegistrationBuilder userHandle(UserHandle userHandle) {
        return recordProperty(this, "userHandle", userHandle);
    }

    public UserRegistrationBuilder phoneNumber(String phoneNumberValue) {
        return phoneNumber(PhoneNumber.phoneNumber(phoneNumberValue));
    }

    public UserRegistrationBuilder phoneNumber(PhoneNumber phoneNumber) {
        return recordProperty(this, "phoneNumber", phoneNumber);
    }

    public UserRegistrationBuilder phoneNumberVerificationCode(String verificationCodeValue) {
        return phoneNumberVerificationCode(VerificationCode.verificationCode(verificationCodeValue));
    }

    public UserRegistrationBuilder phoneNumberVerificationCode(VerificationCode phoneNumberVerificationCode) {
        return recordProperty(this, "phoneNumberVerificationCode", phoneNumberVerificationCode);
    }

    public UserRegistrationBuilder phoneNumberVerified(boolean phoneNumberVerified) {
        return recordProperty(this, "phoneNumberVerified", phoneNumberVerified);
    }

    public UserRegistrationBuilder fullName(String firstName, String lastName) {
        return fullName(FullName.fullName(firstName, lastName));
    }

    public UserRegistrationBuilder fullName(FullName fullName) {
        return recordProperty(this, "fullName", fullName);
    }

    public UserRegistrationBuilder completed(boolean completed) {
        return recordProperty(this, "completed", completed);
    }

    public static UserRegistrationBuilder userRegistration() {
        return new UserRegistrationBuilder();
    }

    public static DomainEventPublisher anyDomainEventPublisher() {
        return mock(DomainEventPublisher.class);
    }

    public static UserRegistrationId anyUserRegistrationId() {
        return userRegistrationId(randomString(32));
    }

    public static UserHandle anyUserHandle() {
        return UserHandle.userHandle(randomString(10));
    }

    public static PhoneNumber anyPhoneNumber() {
        return PhoneNumber.phoneNumber(randomString(16));
    }

    public static VerificationCode anyPhoneNumberVerificationCode() {
        return randomVerificationCode();
    }

    public static FullName anyFullName() {
        return FullName.fullName(randomString(8), randomString(12));
    }

}
