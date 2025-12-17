package com.github.cstettler.dddttc.registration.infrastructure.web;

import com.github.cstettler.dddttc.registration.application.UserRegistrationService;
import com.github.cstettler.dddttc.registration.domain.FullName;
import com.github.cstettler.dddttc.registration.domain.PhoneNumber;
import com.github.cstettler.dddttc.registration.domain.PhoneNumberAlreadyVerifiedException;
import com.github.cstettler.dddttc.registration.domain.PhoneNumberNotSwissException;
import com.github.cstettler.dddttc.registration.domain.PhoneNumberNotYetVerifiedException;
import com.github.cstettler.dddttc.registration.domain.PhoneNumberVerificationCodeInvalidException;
import com.github.cstettler.dddttc.registration.domain.UserHandle;
import com.github.cstettler.dddttc.registration.domain.UserHandleAlreadyInUseException;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationAlreadyCompletedException;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationId;
import com.github.cstettler.dddttc.registration.domain.UserRegistrationNotExistingException;
import com.github.cstettler.dddttc.registration.domain.VerificationCode;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import static com.github.cstettler.dddttc.registration.domain.FullName.fullName;
import static com.github.cstettler.dddttc.registration.domain.PhoneNumber.phoneNumber;
import static com.github.cstettler.dddttc.registration.domain.UserHandle.userHandle;
import static com.github.cstettler.dddttc.registration.domain.UserRegistrationId.userRegistrationId;
import static com.github.cstettler.dddttc.registration.domain.VerificationCode.verificationCode;
import static com.github.cstettler.dddttc.support.infrastructure.web.ModelAndViewBuilder.modelAndView;

@Controller
@RequestMapping("/user-registration")
class UserRegistrationController {

    private final UserRegistrationService userRegistrationService;

    UserRegistrationController(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }

    @GetMapping("/")
    ModelAndView index() {
        return modelAndView("start")
                .property("userHandle", "")
                .build();
    }

    @PostMapping("/start")
    ModelAndView triggerNewUserRegistration(@RequestParam("user-handle") String userHandleValue, @RequestParam("phone-number") String phoneNumberValue) {
        UserHandle userHandle = userHandle(userHandleValue);
        PhoneNumber phoneNumber = phoneNumber(phoneNumberValue);

        try {
            UserRegistrationId userRegistrationId = this.userRegistrationService.startNewUserRegistrationProcess(userHandle, phoneNumber);

            return modelAndView("verify")
                    .property("userRegistrationId", userRegistrationId.value())
                    .property("userHandle", userHandle.value())
                    .build();
        } catch (UserHandleAlreadyInUseException | PhoneNumberNotSwissException e) {
            return modelAndView("start")
                    .property("userHandle", userHandle.value())
                    .property("phoneNumber", phoneNumber.value())
                    .error(e)
                    .build();
        }
    }

    @PostMapping("/verify")
    ModelAndView verifyPhoneNumber(@RequestParam("user-registration-id") String userRegistrationIdValue, @RequestParam("user-handle") String userHandleValue, @RequestParam("verification-code") String verificationCodeValue) {
        UserRegistrationId userRegistrationId = userRegistrationId(userRegistrationIdValue);
        UserHandle userHandle = userHandle(userHandleValue);
        VerificationCode verificationCode = verificationCode(verificationCodeValue);

        try {
            this.userRegistrationService.verifyPhoneNumber(userRegistrationId, verificationCode);

            return modelAndView("complete")
                    .property("userRegistrationId", userRegistrationId.value())
                    .property("userHandle", userHandle.value())
                    .property("firstName", "")
                    .property("lastName", "")
                    .build();
        } catch (PhoneNumberAlreadyVerifiedException e) {
            return modelAndView("complete")
                    .property("userRegistrationId", userRegistrationId.value())
                    .property("userHandle", userHandle.value())
                    .property("firstName", "")
                    .property("lastName", "")
                    .build();
        } catch (PhoneNumberVerificationCodeInvalidException e) {
            return modelAndView("verify")
                    .property("userRegistrationId", userRegistrationId.value())
                    .property("userHandle", userHandle.value())
                    .error(e)
                    .build();
        } catch (UserRegistrationNotExistingException e) {
            return modelAndView("error")
                    .error(e)
                    .build();
        }
    }

    @PostMapping("/complete")
    ModelAndView completeUserRegistration(@RequestParam("user-registration-id") String userRegistrationIdValue, @RequestParam("user-handle") String userHandleValue, @RequestParam("first-name") String firstName, @RequestParam("last-name") String lastName) {
        UserRegistrationId userRegistrationId = userRegistrationId(userRegistrationIdValue);
        UserHandle userHandle = userHandle(userHandleValue);
        FullName fullName = fullName(firstName, lastName);

        try {
            this.userRegistrationService.completeUserRegistration(userRegistrationId, fullName);

            return modelAndView("done")
                    .property("userHandle", userHandle.value())
                    .property("fullName", fullName.firstAndLastName())
                    .build();
        } catch (PhoneNumberNotYetVerifiedException e) {
            return modelAndView("complete")
                    .property("userRegistrationId", userRegistrationId.value())
                    .property("firstName", firstName)
                    .property("lastName", lastName)
                    .error(e)
                    .build();
        } catch (UserRegistrationAlreadyCompletedException e) {
            return modelAndView("done")
                    .property("userHandle", fullName.firstAndLastName())
                    .property("fullName", fullName.firstAndLastName())
                    .build();
        } catch (UserRegistrationNotExistingException e) {
            return modelAndView("error")
                    .error(e)
                    .build();
        }
    }

}
