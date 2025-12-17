package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.DomainEvent;

@DomainEvent
public class UserRegistrationCompletedEvent {

    private final UserRegistrationId userRegistrationId;
    private final UserHandle userHandle;
    private final PhoneNumber phoneNumber;
    private final FullName fullName;

    private UserRegistrationCompletedEvent(UserRegistrationId userRegistrationId, UserHandle userHandle, PhoneNumber phoneNumber, FullName fullName) {
        this.userRegistrationId = userRegistrationId;
        this.userHandle = userHandle;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
    }

    public UserRegistrationId userRegistrationId() {
        return this.userRegistrationId;
    }

    public UserHandle userHandle() {
        return this.userHandle;
    }

    public PhoneNumber phoneNumber() {
        return this.phoneNumber;
    }

    public FullName fullName() {
        return this.fullName;
    }

    public static UserRegistrationCompletedEvent userRegistrationCompleted(UserRegistrationId userRegistrationId, UserHandle userHandle, PhoneNumber phoneNumber, FullName fullName) {
        return new UserRegistrationCompletedEvent(userRegistrationId, userHandle, phoneNumber, fullName);
    }

}
