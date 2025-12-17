package com.github.cstettler.dddttc.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents an infrastructure service. An infrastructure service provides functionality to the domain that requires
 * additional infrastructure only available outside of the domain. The infrastructure service interface forms part of
 * the domain, the implementation is part of the infrastructure.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface InfrastructureService {
}
