package com.github.cstettler.dddttc.registration.application;

import com.github.cstettler.dddttc.registration.domain.FullName;
import com.github.cstettler.dddttc.registration.domain.PhoneNumber;
import com.github.cstettler.dddttc.registration.domain.PhoneNumberAlreadyVerifiedException;
import com.github.cstettler.dddttc.registration.domain.PhoneNumberNotSwissException;
import com.github.cstettler.dddttc.registration.domain.PhoneNumberNotYetVerifiedException;
import com.github.cstettler.dddttc.registration.domain.PhoneNumberVerificationCodeInvalidException;
import com.github.cstettler.dddttc.registration.domain.UserHandle;
import com.github.cstettler.dddttc.registration.domain.UserHandleAlreadyInUseException;
import com.github.cstettler.dddttc.registration.domain.UserRegistration;
import com.github.cstettler.dddttc.registration.domain.UserRegistration.UserRegistrationFactory;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationAlreadyCompletedException;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationId;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationNotExistingException;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationRepository;
import com.github.cstettler.dddttc.registration.domain.VerificationCode;
import com.github.cstettler.dddttc.stereotype.ApplicationService;

@ApplicationService
public class UserRegistrationService {

    private final UserRegistrationFactory userRegistrationFactory;
    private final UserRegistrationRepository userRegistrationRepository;

    UserRegistrationService(UserRegistrationFactory userRegistrationFactory, UserRegistrationRepository userRegistrationRepository) {
        this.userRegistrationFactory = userRegistrationFactory;
        this.userRegistrationRepository = userRegistrationRepository;
    }

    public UserRegistrationId startNewUserRegistrationProcess(UserHandle userHandle, PhoneNumber phoneNumber) throws UserHandleAlreadyInUseException, PhoneNumberNotSwissException {
        UserRegistration userRegistration = this.userRegistrationFactory.newUserRegistration(userHandle, phoneNumber);
        UserRegistrationId userRegistrationId = userRegistration.id();

        this.userRegistrationRepository.add(userRegistration);

        return userRegistrationId;
    }

    public void verifyPhoneNumber(UserRegistrationId userRegistrationId, VerificationCode verificationCode) throws UserRegistrationNotExistingException, PhoneNumberVerificationCodeInvalidException, PhoneNumberAlreadyVerifiedException {
        UserRegistration userRegistration = this.userRegistrationRepository.get(userRegistrationId);
        userRegistration.verifyPhoneNumber(verificationCode);

        this.userRegistrationRepository.update(userRegistration);
    }

    public void completeUserRegistration(UserRegistrationId userRegistrationId, FullName fullName) throws UserRegistrationNotExistingException, PhoneNumberNotYetVerifiedException, UserRegistrationAlreadyCompletedException {
        UserRegistration userRegistration = this.userRegistrationRepository.get(userRegistrationId);
        userRegistration.complete(fullName);

        this.userRegistrationRepository.update(userRegistration);
    }

}
