package com.github.cstettler.dddttc.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents an event relevant to the business domain. A domain event describes a fact that has happened in the past.
 * Domain events are immutable and carry the state relevant for consumers to understand the semantics of the domain
 * event.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface DomainEvent {

    /**
     * Type of the domain event. Must be defined for cross-bounded context domain events, but are is not mandatory for
     * intra-bounded context domain events. If defined, the type must be unique within the whole system. If not defined,
     * the type of the domain event is derived from the domain event class.
     */
    String value() default "";

}
