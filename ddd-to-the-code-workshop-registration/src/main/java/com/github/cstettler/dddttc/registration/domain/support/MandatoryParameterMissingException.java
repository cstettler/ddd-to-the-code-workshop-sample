package com.github.cstettler.dddttc.registration.domain.support;

import com.github.cstettler.dddttc.stereotype.BusinessException;

@BusinessException
public class MandatoryParameterMissingException extends RuntimeException {

    private MandatoryParameterMissingException(String parameterName) {
        super("mandatory parameter '" + parameterName + "' is missing");
    }

    static MandatoryParameterMissingException mandatoryPropertyMissing(String parameterName) {
        return new MandatoryParameterMissingException(parameterName);
    }

}
