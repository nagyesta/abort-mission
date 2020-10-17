package com.github.nagyesta.abortmission.core.matcher.impl.builder;

/**
 * Interface used for starting the builder calls.
 */
public interface InitialMissionHealthCheckMatcherBuilder
        extends ClassMatcherBuilder, DependencyMatcherBuilder, EnvironmentMatcherBuilder, SystemPropertyMatcherBuilder,
        InitialAndMatcherBuilder, InitialOrMatcherBuilder, NotMatcherBuilder {
}
