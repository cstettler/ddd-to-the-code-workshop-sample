package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.stereotype.InfrastructureService;

@InfrastructureService
public interface SmsNotificationSender {

    void sendSmsTo(PhoneNumber phoneNumber, String text);

}
