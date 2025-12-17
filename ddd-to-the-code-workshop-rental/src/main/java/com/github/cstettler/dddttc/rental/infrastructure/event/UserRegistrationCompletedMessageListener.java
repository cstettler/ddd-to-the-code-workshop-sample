package com.github.cstettler.dddttc.rental.infrastructure.event;

import com.github.cstettler.dddttc.rental.application.UserService;
import com.github.cstettler.dddttc.rental.domain.user.UserAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import static com.github.cstettler.dddttc.rental.domain.user.FirstName.firstName;
import static com.github.cstettler.dddttc.rental.domain.user.LastName.lastName;
import static com.github.cstettler.dddttc.rental.domain.user.UserId.userId;

@Component
class UserRegistrationCompletedMessageListener {

    private static Logger LOGGER = LoggerFactory.getLogger(UserRegistrationCompletedMessageListener.class);

    private final UserService userService;

    UserRegistrationCompletedMessageListener(UserService userService) {
        this.userService = userService;
    }

    @JmsListener(destination = "registration/user-registration-completed")
    public void onUserRegistrationCompleted(UserRegistrationCompletedMessage message) {
        String userHandle = message.userHandle.value;
        String firstName = message.fullName.firstName;
        String lastName = message.fullName.lastName;

        LOGGER.info("received user registration completed message for user handle '" + userHandle + "'");

        try {
            this.userService.addUser(userId(userHandle), firstName(firstName), lastName(lastName));

            LOGGER.info("added user '" + userHandle + "'");
        } catch (UserAlreadyExistsException e) {
            // ignored
        }
    }


    public static class UserRegistrationCompletedMessage {

        ValueWrapper userHandle;
        FullName fullName;

    }


    public static class FullName {

        String firstName;
        String lastName;

    }


    public static class ValueWrapper {

        String value;

    }

}
