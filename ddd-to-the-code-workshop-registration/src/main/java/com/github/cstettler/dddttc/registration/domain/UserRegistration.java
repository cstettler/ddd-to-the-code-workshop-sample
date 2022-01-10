package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.Aggregate;
import com.github.cstettler.dddttc.stereotype.AggregateFactory;
import com.github.cstettler.dddttc.stereotype.AggregateId;
import com.github.cstettler.dddttc.support.domain.DomainEventPublisher;

import static com.github.cstettler.dddttc.registration.domain.PhoneNumberAlreadyVerifiedException.phoneNumberAlreadyVerified;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumberNotSwissException.phoneNumberNotSwiss;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumberNotYetVerifiedException.phoneNumberNotYetVerified;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumberVerificationCodeGeneratedEvent.phoneNumberVerificationCodeGenerated;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumberVerificationCodeInvalidException.phoneNumberVerificationCodeInvalid;
import static com.github.cstettler.dddttc.registration.domain.UserHandleAlreadyInUseException.userHandleAlreadyInUse;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationAlreadyCompletedException.userRegistrationAlreadyCompleted;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationCompletedEvent.userRegistrationCompleted;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationId.newUserRegistrationId;
import static com.github.cstettler.dddttc.registration.domain.VerificationCode.randomVerificationCode;
import static com.github.cstettler.dddttc.registration.domain.support.Validations.validateNotNull;

@Aggregate
public class UserRegistration {

    private final transient DomainEventPublisher domainEventPublisher;
    private final UserRegistrationId id;
    private final UserHandle userHandle;
    private final PhoneNumber phoneNumber;
    private final VerificationCode phoneNumberVerificationCode;
    private FullName fullName;
    private boolean phoneNumberVerified;
    private boolean completed;

    private UserRegistration(DomainEventPublisher domainEventPublisher, UserHandle userHandle, PhoneNumber phoneNumber) {
        validateNotNull("userHandle", userHandle);
        validateNotNull("phoneNumber", phoneNumber);

        if (!(phoneNumber.isSwiss())) {
            throw phoneNumberNotSwiss(phoneNumber);
        }

        this.domainEventPublisher = domainEventPublisher;
        this.id = newUserRegistrationId();
        this.userHandle = userHandle;
        this.phoneNumber = phoneNumber;
        this.phoneNumberVerificationCode = randomVerificationCode();

        this.phoneNumberVerified = false;
        this.completed = false;

        this.domainEventPublisher.publish(phoneNumberVerificationCodeGenerated(this.phoneNumber, this.phoneNumberVerificationCode));
    }

    @AggregateId
    public UserRegistrationId id() {
        return this.id;
    }

    public UserHandle userHandle() {
        return this.userHandle;
    }

    public PhoneNumber phoneNumber() {
        return this.phoneNumber;
    }

    public FullName fullName() {
        return this.fullName;
    }

    public void verifyPhoneNumber(VerificationCode verificationCode) throws PhoneNumberVerificationCodeInvalidException, PhoneNumberAlreadyVerifiedException {
        validateNotNull("verificationCode", verificationCode);

        if (this.phoneNumberVerified) {
            throw phoneNumberAlreadyVerified(this.phoneNumber);
        }

        if (!this.phoneNumberVerificationCode.matches(verificationCode)) {
            throw phoneNumberVerificationCodeInvalid(verificationCode);
        }

        this.phoneNumberVerified = true;
    }

    public void complete(FullName fullName) throws PhoneNumberNotYetVerifiedException, UserRegistrationAlreadyCompletedException {
        validateNotNull("fullName", fullName);

        if (!(this.phoneNumberVerified)) {
            throw phoneNumberNotYetVerified(this.phoneNumber);
        }

        if (this.completed) {
            throw userRegistrationAlreadyCompleted(this.id);
        }

        this.fullName = fullName;
        this.completed = true;

        this.domainEventPublisher.publish(userRegistrationCompleted(this.id, this.userHandle, this.phoneNumber, this.fullName));
    }


    @AggregateFactory(UserRegistration.class)
    public static class UserRegistrationFactory {

        private final UserRegistrationRepository userRegistrationRepository;
        private final DomainEventPublisher domainEventPublisher;

        UserRegistrationFactory(UserRegistrationRepository userRegistrationRepository, DomainEventPublisher domainEventPublisher) {
            this.userRegistrationRepository = userRegistrationRepository;
            this.domainEventPublisher = domainEventPublisher;
        }

        public UserRegistration newUserRegistration(UserHandle userHandle, PhoneNumber phoneNumber) throws UserHandleAlreadyInUseException {
            if (this.userRegistrationRepository.find(userHandle) != null) {
                throw userHandleAlreadyInUse(userHandle);
            }

            return new UserRegistration(this.domainEventPublisher, userHandle, phoneNumber);
        }

    }

}
