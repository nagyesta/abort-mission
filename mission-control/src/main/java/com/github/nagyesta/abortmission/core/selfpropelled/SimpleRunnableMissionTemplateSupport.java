package com.github.nagyesta.abortmission.core.selfpropelled;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implements the base parts of a simplified {@link Runnable} mission template (without preparation steps).
 */
public abstract class SimpleRunnableMissionTemplateSupport extends RunnableMissionTemplateSupport<Optional<Void>> {

    /**
     * Constructor passing through initialization parameters to the superclass.
     *
     * @param contextName     The name of the mission context
     * @param evaluationScope The class we want to use as the annotated test class which will be used by matchers.
     * @param abortSequence   The {@link Runnable} abort sequence (typically throwing an exception).
     */
    protected SimpleRunnableMissionTemplateSupport(
            final String contextName,
            final Class<?> evaluationScope,
            final Runnable abortSequence) {
        super(contextName, evaluationScope, abortSequence);
    }

    @Override
    public Supplier<Optional<Void>> preLaunchPreparationSupplier() {
        return Optional::empty;
    }
}
