package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class PhoneNumberNotSwissException extends RuntimeException {

    private PhoneNumberNotSwissException(PhoneNumber phoneNumber) {
        super("phone number '" + phoneNumber.value() + "' is not swiss");
    }

    static PhoneNumberNotSwissException phoneNumberNotSwiss(PhoneNumber phoneNumber) {
        return new PhoneNumberNotSwissException(phoneNumber);
    }

}
