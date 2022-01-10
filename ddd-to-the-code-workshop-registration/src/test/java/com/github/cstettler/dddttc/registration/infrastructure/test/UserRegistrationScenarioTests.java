package com.github.cstettler.dddttc.registration.infrastructure.test;

import com.github.cstettler.dddttc.registration.application.UserRegistrationService;
import com.github.cstettler.dddttc.registration.domain.FullName;
import com.github.cstettler.dddttc.registration.domain.PhoneNumber;
import com.github.cstettler.dddttc.registration.domain.PhoneNumberVerificationCodeGeneratedEvent;
import com.github.cstettler.dddttc.registration.domain.UserHandle;
import com.github.cstettler.dddttc.registration.domain.UserRegistration;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationId;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationRepository;
import com.github.cstettler.dddttc.registration.domain.VerificationCode;
import com.github.cstettler.dddttc.support.test.ScenarioTest;
import com.github.cstettler.dddttc.support.test.WithDomainEventTestSupport.DomainEventRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static com.github.cstettler.dddttc.registration.domain.FullName.fullName;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumber.phoneNumber;
import static com.github.cstettler.dddttc.registration.domain.UserHandle.userHandle;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationMatcher.userRegistrationWith;
import static com.github.cstettler.dddttc.support.ReflectionBasedStateMatcher.hasState;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@ScenarioTest
class UserRegistrationScenarioTests {

    @Autowired
    private UserRegistrationService userRegistrationService;

    @Autowired
    private UserRegistrationRepository userRegistrationRepository;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    void registerNewUser(DomainEventRecorder domainEventRecorder) {
        // arrange
        UserHandle userHandle = userHandle("peter");
        PhoneNumber phoneNumber = phoneNumber("+41 79 123 45 67");

        // act
        UserRegistrationId userRegistrationId = this.userRegistrationService.startNewUserRegistrationProcess(userHandle, phoneNumber);

        // arrange
        PhoneNumberVerificationCodeGeneratedEvent event = domainEventRecorder.singleRecordedDomainEvent();
        VerificationCode verificationCode = event.verificationCode();

        // act
        this.userRegistrationService.verifyPhoneNumber(userRegistrationId, verificationCode);

        // arrange
        FullName fullName = fullName("Peter", "Meier");

        // act
        this.userRegistrationService.completeUserRegistration(userRegistrationId, fullName);

        // assert
        invokeInTransaction(() -> {
            UserRegistration completedUserRegistration = this.userRegistrationRepository.get(userRegistrationId);
            assertThat(completedUserRegistration, hasState(userRegistrationWith()
                    .id(is(userRegistrationId))
                    .userHandle(is(userHandle("peter")))
                    .phoneNumber(is(phoneNumber("+41 79 123 45 67")))
                    .phoneNumberVerified(is(true))
                    .fullName(is(fullName("Peter", "Meier")))
                    .completed(is(true))
            ));
        });
    }

    private void invokeInTransaction(Executable executable) {
        new TransactionTemplate(this.transactionManager).execute((status) -> {
            try {
                executable.execute();

                return null;
            } catch (Throwable t) {
                throw new IllegalStateException("unable to execute in transaction", t);
            }
        });
    }

}
