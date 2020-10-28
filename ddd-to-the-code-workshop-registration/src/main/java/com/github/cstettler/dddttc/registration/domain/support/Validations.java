package com.github.cstettler.dddttc.registration.domain.support;

import static com.github.cstettler.dddttc.registration.domain.support.MandatoryParameterMissingException.mandatoryPropertyMissing;

public class Validations {

    private Validations() {
    }

    public static void validateNotNull(String parameterName, Object parameterValue) {
        if (parameterValue == null) {
            throw mandatoryPropertyMissing(parameterName);
        }
    }

}
