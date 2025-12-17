package com.github.cstettler.dddttc.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;

/**
 * Represents an aggregate with core domain logic and related state. An aggregate ensures its domain invariants and
 * represents the minimal scope of a business transaction. Aggregate instances are identified by their aggregate id.
 * Equality between two aggregates instances is defined by their type and their aggregate id.
 */
@Target(TYPE)
@Documented
public @interface Aggregate {

}
