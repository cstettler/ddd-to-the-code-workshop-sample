package com.github.cstettler.dddttc.rental.domain.user;

public class UserAlreadyExistsException extends RuntimeException {

    private UserAlreadyExistsException(UserId userId) {
        super("user '" + userId.value() + "' already exists");
    }

    public static UserAlreadyExistsException userAlreadyExists(UserId userId) {
        return new UserAlreadyExistsException(userId);
    }

}
