package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.DomainEvent;

@DomainEvent
public class PhoneNumberVerificationCodeGeneratedEvent {

    private final PhoneNumber phoneNumber;
    private final VerificationCode verificationCode;

    private PhoneNumberVerificationCodeGeneratedEvent(PhoneNumber phoneNumber, VerificationCode verificationCode) {
        this.phoneNumber = phoneNumber;
        this.verificationCode = verificationCode;
    }

    public PhoneNumber phoneNumber() {
        return this.phoneNumber;
    }

    public VerificationCode verificationCode() {
        return this.verificationCode;
    }

    static PhoneNumberVerificationCodeGeneratedEvent phoneNumberVerificationCodeGenerated(PhoneNumber phoneNumber, VerificationCode verificationCode) {
        return new PhoneNumberVerificationCodeGeneratedEvent(phoneNumber, verificationCode);
    }

}
