package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class PhoneNumberVerificationCodeInvalidException extends RuntimeException {

    private PhoneNumberVerificationCodeInvalidException(VerificationCode verificationCode) {
        super("phone number verification code '" + verificationCode.value() + "' is invalid");
    }

    static PhoneNumberVerificationCodeInvalidException phoneNumberVerificationCodeInvalid(VerificationCode verificationCode) {
        return new PhoneNumberVerificationCodeInvalidException(verificationCode);
    }

}
