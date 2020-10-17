package com.github.nagyesta.abortmission.core.annotation;

import java.lang.annotation.*;

/**
 * Defines the {@link Exception} types which should not be reported as failures even if they fail the tests.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SuppressLaunchFailureReporting {

    /**
     * The list of {@link Exception} types we want to ignore.
     *
     * @return suppressed exceptions
     */
    Class<? extends Exception>[] forExceptions();
}
