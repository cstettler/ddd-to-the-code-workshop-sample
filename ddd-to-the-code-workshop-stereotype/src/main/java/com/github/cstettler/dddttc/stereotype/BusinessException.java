package com.github.cstettler.dddttc.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Represents a business exception. Business exceptions signal attempts to invalidly change business invariants.
 */
@Target(TYPE)
@Documented
public @interface BusinessException {

}
