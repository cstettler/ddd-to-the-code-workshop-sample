package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.support.ReflectionBasedStateMatcher;
import org.hamcrest.Matcher;

import java.util.Map;

public class UserRegistrationMatcher extends ReflectionBasedStateMatcher<UserRegistration> {

    private UserRegistrationMatcher(Map<String, Matcher<?>> matchersByPropertyName) {
        super("user registration", matchersByPropertyName);
    }

    public static Builder userRegistrationWith() {
        return new Builder();
    }


    public static class Builder extends MatcherBuilder<UserRegistrationMatcher> {

        public Builder id(Matcher<?> idMatcher) {
            return recordPropertyMatcher(this, "id", idMatcher);
        }

        public Builder userHandle(Matcher<?> userHandleMatcher) {
            return recordPropertyMatcher(this, "userHandle", userHandleMatcher);
        }

        public Builder phoneNumber(Matcher<?> phoneNumberMatcher) {
            return recordPropertyMatcher(this, "phoneNumber", phoneNumberMatcher);
        }

        public Builder phoneNumberVerificationCode(Matcher<?> phoneNumberVerificationCodeMatcher) {
            return recordPropertyMatcher(this, "phoneNumberVerificationCode", phoneNumberVerificationCodeMatcher);
        }

        public Builder phoneNumberVerified(Matcher<?> phoneNumberVerifiedMatcher) {
            return recordPropertyMatcher(this, "phoneNumberVerified", phoneNumberVerifiedMatcher);
        }

        public Builder fullName(Matcher<?> fullNameMatcher) {
            return recordPropertyMatcher(this, "fullName", fullNameMatcher);
        }

        public Builder completed(Matcher<?> completedMatcher) {
            return recordPropertyMatcher(this, "completed", completedMatcher);
        }

        @Override
        protected UserRegistrationMatcher build(Map<String, Matcher<?>> matchersByPropertyName) {
            return new UserRegistrationMatcher(matchersByPropertyName);
        }

    }

}
