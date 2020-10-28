package com.github.cstettler.dddttc.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents a factory responsible for creating new aggregate instances. An aggregate factory encapsulates the
 * knowledge required to create a new aggregate instance. An aggregate factory can either be a static method on an
 * aggregate, or a separate class, depending on the complexity of the instantiation process and the dependencies needed.
 */
@Target({TYPE, METHOD})
@Retention(RUNTIME)
@Documented
public @interface AggregateFactory {

    /**
     * The type of aggregate created
     */
    Class<?> value();

}
