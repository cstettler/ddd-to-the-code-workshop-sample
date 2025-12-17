package com.github.cstettler.dddttc.registration.infrastructure.sms;

import com.github.cstettler.dddttc.registration.domain.PhoneNumber;
import com.github.cstettler.dddttc.registration.domain.SmsNotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class LoggingSmsNotificationSender implements SmsNotificationSender {

    private static Logger LOGGER = LoggerFactory.getLogger(LoggingSmsNotificationSender.class);

    @Override
    public void sendSmsTo(PhoneNumber phoneNumber, String text) {
        LOGGER.info("SMS to '" + phoneNumber.value() + "': '" + text + "'");
    }

}
