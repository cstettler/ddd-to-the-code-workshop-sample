package com.github.cstettler.dddttc.rental.domain.user;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class UserNotExistingException extends RuntimeException {

    private UserNotExistingException(UserId userid) {
        super("user '" + userid.value() + "' does not exist");
    }

    public static UserNotExistingException userNotExisting(UserId userid) {
        return new UserNotExistingException(userid);
    }

}
