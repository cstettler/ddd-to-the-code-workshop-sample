package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class PhoneNumberAlreadyVerifiedException extends RuntimeException {

    private PhoneNumberAlreadyVerifiedException(PhoneNumber phoneNumber) {
        super("phone number '" + phoneNumber.value() + "' has already been verified");
    }

    static PhoneNumberAlreadyVerifiedException phoneNumberAlreadyVerified(PhoneNumber phoneNumber) {
        return new PhoneNumberAlreadyVerifiedException(phoneNumber);
    }

}
