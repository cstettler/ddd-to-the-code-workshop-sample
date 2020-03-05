package com.github.cstettler.dddttc.rental.domain.user;

import com.github.cstettler.dddttc.stereotype.Repository;

@Repository
public interface UserRepository {

    void add(User user) throws UserAlreadyExistsException;

    User get(UserId userId) throws UserNotExistingException;

}
