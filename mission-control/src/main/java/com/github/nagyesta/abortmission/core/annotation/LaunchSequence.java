package com.github.nagyesta.abortmission.core.annotation;

import com.github.nagyesta.abortmission.core.outline.MissionOutline;

import java.lang.annotation.*;

/**
 * Defines the class that needs ot be used for the initialization of our matchers and evaluators.
 * <p>
 * When this annotation is used, the referenced class will be used for initialization, otherwise
 * the automatic resolution logic will start to backtrack from the package of the test class to
 * the root package and will look for a {@link MissionOutline} subclass named MissionOutlineDefinition.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LaunchSequence {

    /**
     * The {@link Class} we need to use as init class. Must have a public default constructor.
     *
     * @return The init class.
     */
    Class<? extends MissionOutline> value();
}
