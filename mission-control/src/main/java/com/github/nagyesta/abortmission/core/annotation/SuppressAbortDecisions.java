package com.github.nagyesta.abortmission.core.annotation;

import java.lang.annotation.*;

/**
 * Marker annotation indicating that abort decisions should be overridden and abort should not be triggered in the context
 * of the annotated element.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface SuppressAbortDecisions {

}
