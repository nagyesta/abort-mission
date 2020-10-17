package com.github.nagyesta.abortmission.core.selfpropelled;

import java.util.concurrent.Callable;
import java.util.function.Function;

/**
 * Defines how a {@link java.util.concurrent.Callable} based mission template implementation should behave.
 *
 * @param <P> The type we want to produce as the result of the test preparation and pass to the test phase.
 * @param <T> The type of the result we want to produce by the end of the test phase.
 */
public interface CallableMissionTemplate<P, T> extends PreLaunchMissionTemplate<P>, Callable<T> {

    /**
     * Returns a {@link Function} that converts the result of the preparation step to the test results by executing the test method.
     *
     * @return the function.
     */
    Function<P, T> missionPayloadFunction();
}
