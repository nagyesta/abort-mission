package com.github.nagyesta.abortmission.core.selfpropelled;

import java.util.function.Supplier;

/**
 * Adds pre-launch evaluation support to the template.
 *
 * @param <P> The type of the context created as the result of the preparation.
 */
public interface PreLaunchMissionTemplate<P> {

    /**
     * Returns a {@link Supplier} which will put together the input of the test execution while running the preparation steps.
     *
     * @return the supplier
     */
    Supplier<P> preLaunchPreparationSupplier();
}
