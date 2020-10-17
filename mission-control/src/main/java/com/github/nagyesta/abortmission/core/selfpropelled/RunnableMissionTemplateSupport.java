package com.github.nagyesta.abortmission.core.selfpropelled;

import java.util.Optional;

/**
 * Implements the base parts of a {@link Runnable} mission template.
 *
 * @param <P> The type we want to produce as the result of the test preparation and pass to the test phase.
 */
public abstract class RunnableMissionTemplateSupport<P> extends AbstractMissionTemplate<P, Void> implements RunnableMissionTemplate<P> {

    /**
     * Constructor passing through initialization parameters to the superclass.
     *
     * @param contextName     The name of the mission context
     * @param evaluationScope The class we want to use as the annotated test class which will be used by matchers.
     * @param abortSequence   The {@link Runnable} abort sequence (typically throwing an exception).
     */
    public RunnableMissionTemplateSupport(final String contextName,
                                          final Class<?> evaluationScope,
                                          final Runnable abortSequence) {
        super(contextName, evaluationScope, abortSequence);
    }

    @Override
    protected Optional<Void> doLaunch(final P preparedContext) {
        missionPayloadConsumer().accept(preparedContext);
        return Optional.empty();
    }

    @Override
    public void run() {
        executeTemplate();
    }
}
