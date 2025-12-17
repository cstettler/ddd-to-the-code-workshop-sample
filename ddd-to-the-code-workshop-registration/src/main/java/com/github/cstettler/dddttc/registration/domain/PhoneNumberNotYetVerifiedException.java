package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class PhoneNumberNotYetVerifiedException extends RuntimeException {

    private PhoneNumberNotYetVerifiedException(PhoneNumber phoneNumber) {
        super("phone number '" + phoneNumber.value() + "' has not yet been verified");
    }

    static PhoneNumberNotYetVerifiedException phoneNumberNotYetVerified(PhoneNumber phoneNumber) {
        return new PhoneNumberNotYetVerifiedException(phoneNumber);
    }

}
