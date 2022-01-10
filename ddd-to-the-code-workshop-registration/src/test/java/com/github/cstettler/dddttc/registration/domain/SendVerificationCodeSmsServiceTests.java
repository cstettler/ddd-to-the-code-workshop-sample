package com.github.cstettler.dddttc.registration.domain;

import org.junit.jupiter.api.Test;

import static com.github.cstettler.dddttc.registration.domain.PhoneNumber.phoneNumber;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumberVerificationCodeGeneratedEvent.phoneNumberVerificationCodeGenerated;
import static com.github.cstettler.dddttc.registration.domain.VerificationCode.verificationCode;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SendVerificationCodeSmsServiceTests {

    @Test
    void sendVerificationCodeSmsToPhoneNumber_verificationCodeGeneratedEventReceived_invokesSmsNotificationService() {
        // arrange
        PhoneNumber phoneNumber = phoneNumber("+41 79 123 45 67");
        VerificationCode verificationCode = verificationCode("123456");
        PhoneNumberVerificationCodeGeneratedEvent event = phoneNumberVerificationCodeGenerated(phoneNumber, verificationCode);

        SmsNotificationSender smsNotificationSender = smsNotificationSender();
        SendVerificationCodeSmsService sendVerificationCodeSmsService = new SendVerificationCodeSmsService(smsNotificationSender);

        // act
        sendVerificationCodeSmsService.sendVerificationCodeSmsToPhoneNumber(event);

        // assert
        verify(smsNotificationSender).sendSmsTo(eq(phoneNumber), contains("123456"));
    }

    private static SmsNotificationSender smsNotificationSender() {
        return mock(SmsNotificationSender.class);
    }

}