package com.github.cstettler.dddttc.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents an application service responsible for providing access to the domain to external clients. An application
 * service orchestrates use cases, but does not contain business logic.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface ApplicationService {

}
