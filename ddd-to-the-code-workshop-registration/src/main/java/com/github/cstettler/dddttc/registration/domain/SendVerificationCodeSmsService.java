package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.DomainEventHandler;
import com.github.cstettler.dddttc.stereotype.DomainService;

@DomainService
class SendVerificationCodeSmsService {

    private final SmsNotificationSender smsNotificationSender;

    SendVerificationCodeSmsService(SmsNotificationSender smsNotificationSender) {
        this.smsNotificationSender = smsNotificationSender;
    }

    @DomainEventHandler
    void sendVerificationCodeSmsToPhoneNumber(PhoneNumberVerificationCodeGeneratedEvent event) {
        PhoneNumber phoneNumber = event.phoneNumber();
        VerificationCode verificationCode = event.verificationCode();

        String smsText = "Your verification code is " + verificationCode.value();

        this.smsNotificationSender.sendSmsTo(phoneNumber, smsText);
    }

}
