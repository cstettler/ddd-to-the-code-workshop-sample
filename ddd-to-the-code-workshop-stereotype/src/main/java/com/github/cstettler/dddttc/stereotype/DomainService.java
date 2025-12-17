package com.github.cstettler.dddttc.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents a domain service implementing business logic not directly assignable to a specific aggregate. Domain
 * services may also provide domain event handlers.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface DomainService {

}
