package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class UserRegistrationNotExistingException extends RuntimeException {

    private UserRegistrationNotExistingException(UserRegistrationId userRegistrationId) {
        super("user registration '" + userRegistrationId.value() + "' does not exist");
    }

    public static UserRegistrationNotExistingException userRegistrationNotExisting(UserRegistrationId userRegistrationId) {
        return new UserRegistrationNotExistingException(userRegistrationId);
    }

}
