package com.github.nagyesta.abortmission.core.selfpropelled;

import java.util.Optional;
import java.util.function.Supplier;

/**
 * Implements the base parts of a simplified {@link java.util.concurrent.Callable} mission template (without preparation steps).
 *
 * @param <T> The type of the result we want to produce by the end of the test phase.
 */
public abstract class SimpleCallableMissionTemplateSupport<T> extends CallableMissionTemplateSupport<Optional<Void>, T> {

    /**
     * Constructor passing through initialization parameters to the superclass.
     *
     * @param contextName     The name of the mission context
     * @param evaluationScope The class we want to use as the annotated test class which will be used by matchers.
     * @param abortSequence   The {@link Runnable} abort sequence (typically throwing an exception).
     */
    public SimpleCallableMissionTemplateSupport(final String contextName,
                                                final Class<?> evaluationScope,
                                                final Runnable abortSequence) {
        super(contextName, evaluationScope, abortSequence);
    }

    @Override
    public Supplier<Optional<Void>> preLaunchPreparationSupplier() {
        return Optional::empty;
    }
}
