package com.github.cstettler.dddttc.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents a handler of a domain event. A domain event handler consumes domain events of a specific type. Domain
 * event handlers are responsible for dealing with receiving the same domain event multiple times (at-least-once
 * semantics).
 * <p>
 * Domain event handlers are typically part of domain services. A domain event handler method must accept a single
 * parameter of the domain event type handled.
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface DomainEventHandler {

}
