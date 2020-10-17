package com.github.nagyesta.abortmission.core.selfpropelled;

import java.util.function.Consumer;

/**
 * Defines how a {@link Runnable} based mission template implementation should behave.
 *
 * @param <P> The type we want to produce as the result of the test preparation and pass to the test phase.
 */
public interface RunnableMissionTemplate<P> extends PreLaunchMissionTemplate<P>, Runnable {

    /**
     * Returns a {@link Consumer} that can consume the result of the preparation step to execute the test method.
     *
     * @return the consumer
     */
    Consumer<P> missionPayloadConsumer();
}
