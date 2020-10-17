package com.github.nagyesta.abortmission.core.annotation;

import java.lang.annotation.*;

/**
 * Defines the list of dependencies we need for running the test method or test class.
 * <p>
 * If the annotation is present both on the class and the method level, the lists will be merged.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MissionDependencies {

    /**
     * The list of dependencies we need for running the annotated element.
     *
     * @return The dependency names.
     */
    String[] value();
}
