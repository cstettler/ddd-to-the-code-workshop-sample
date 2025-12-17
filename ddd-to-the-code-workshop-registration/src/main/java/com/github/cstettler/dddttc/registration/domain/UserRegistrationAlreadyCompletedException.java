package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class UserRegistrationAlreadyCompletedException extends RuntimeException {

    private UserRegistrationAlreadyCompletedException(UserRegistrationId userRegistrationId) {
        super("user registration '" + userRegistrationId.value() + "' has already been completed");
    }

    static UserRegistrationAlreadyCompletedException userRegistrationAlreadyCompleted(UserRegistrationId userRegistrationId) {
        return new UserRegistrationAlreadyCompletedException(userRegistrationId);
    }

}
