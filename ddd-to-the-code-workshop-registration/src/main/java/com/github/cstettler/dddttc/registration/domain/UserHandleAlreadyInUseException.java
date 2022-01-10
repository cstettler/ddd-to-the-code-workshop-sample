package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class UserHandleAlreadyInUseException extends RuntimeException {

    private UserHandleAlreadyInUseException(UserHandle userHandle) {
        super("user handle '" + userHandle.value() + "' is already in use");
    }

    public static UserHandleAlreadyInUseException userHandleAlreadyInUse(UserHandle userHandle) {
        return new UserHandleAlreadyInUseException(userHandle);
    }

}
