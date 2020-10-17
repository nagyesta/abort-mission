package com.github.nagyesta.abortmission.core.selfpropelled;

import java.util.Optional;

/**
 * Implements the base parts of a {@link java.util.concurrent.Callable} mission template.
 *
 * @param <P> The type we want to produce as the result of the test preparation and pass to the test phase.
 * @param <T> The type of the result we want to produce by the end of the test phase.
 */
public abstract class CallableMissionTemplateSupport<P, T> extends AbstractMissionTemplate<P, T> implements CallableMissionTemplate<P, T> {

    /**
     * Constructor passing through initialization parameters to the superclass.
     *
     * @param contextName     The name of the mission context
     * @param evaluationScope The class we want to use as the annotated test class which will be used by matchers.
     * @param abortSequence   The {@link Runnable} abort sequence (typically throwing an exception).
     */
    public CallableMissionTemplateSupport(final String contextName,
                                          final Class<?> evaluationScope,
                                          final Runnable abortSequence) {
        super(contextName, evaluationScope, abortSequence);
    }

    @Override
    protected Optional<T> doLaunch(final P preparedContext) {
        return Optional.ofNullable(missionPayloadFunction().apply(preparedContext));
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public T call() throws Exception {
        return executeTemplate().orElse(null);
    }
}
