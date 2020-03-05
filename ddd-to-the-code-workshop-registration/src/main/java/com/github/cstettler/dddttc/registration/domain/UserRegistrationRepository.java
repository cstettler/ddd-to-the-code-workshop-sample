package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.Repository;

@Repository
public interface UserRegistrationRepository {

    void add(UserRegistration userRegistration) throws UserHandleAlreadyInUseException;

    void update(UserRegistration userRegistration) throws UserRegistrationNotExistingException;

    UserRegistration get(UserRegistrationId userRegistrationId) throws UserRegistrationNotExistingException;

    UserRegistration find(UserHandle userHandle);

}
