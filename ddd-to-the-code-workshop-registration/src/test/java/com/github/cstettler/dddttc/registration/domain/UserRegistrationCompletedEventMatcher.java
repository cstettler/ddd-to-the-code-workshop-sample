package com.github.cstettler.dddttc.registration.domain;

import com.github.cstettler.dddttc.support.ReflectionBasedStateMatcher;
import org.hamcrest.Matcher;

import java.util.Map;

public class UserRegistrationCompletedEventMatcher extends ReflectionBasedStateMatcher<UserRegistrationCompletedEvent> {

    private UserRegistrationCompletedEventMatcher(Map<String, Matcher<?>> matchersByPropertyName) {
        super("user registration completed event", matchersByPropertyName);
    }

    public static Builder userRegistrationCompletedEventWith() {
        return new Builder();
    }


    public static class Builder extends MatcherBuilder<UserRegistrationCompletedEventMatcher> {

        public Builder id(Matcher<?> idMatcher) {
            return recordPropertyMatcher(this, "id", idMatcher);
        }

        public Builder userHandle(Matcher<?> userHandleMatcher) {
            return recordPropertyMatcher(this, "userHandle", userHandleMatcher);
        }

        public Builder phoneNumber(Matcher<?> phoneNumberMatcher) {
            return recordPropertyMatcher(this, "phoneNumber", phoneNumberMatcher);
        }

        public Builder fullName(Matcher<?> fullNameMatcher) {
            return recordPropertyMatcher(this, "fullName", fullNameMatcher);
        }

        @Override
        protected UserRegistrationCompletedEventMatcher build(Map<String, Matcher<?>> matchersByPropertyName) {
            return new UserRegistrationCompletedEventMatcher(matchersByPropertyName);
        }

    }

}

