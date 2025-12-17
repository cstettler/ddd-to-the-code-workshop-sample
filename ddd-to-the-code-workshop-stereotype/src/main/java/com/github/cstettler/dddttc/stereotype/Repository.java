package com.github.cstettler.dddttc.stereotype;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Represents a repository for aggregates. A repository is responsible for accepting aggregates of a specific type,
 * keeping them and returning the as requested by the domain. The repository interface forms part of the domain, the
 * implementation (e.g. against a database) is part of the infrastructure.
 */
@Target(TYPE)
@Retention(RUNTIME)
@Documented
public @interface Repository {

}
