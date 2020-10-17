package com.github.nagyesta.abortmission.core.matcher.impl;

import com.github.nagyesta.abortmission.core.extractor.DependencyNameExtractor;
import com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher;
import com.github.nagyesta.abortmission.core.matcher.impl.builder.*;

import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Builder class for all {@link MissionHealthCheckMatcher} instances.
 */
@SuppressWarnings({"checkstyle:HiddenField", "checkstyle:DesignForExtension"})
public final class MissionHealthCheckMatcherBuilder
        implements InitialMissionHealthCheckMatcherBuilder, OrMatcherBuilder, AndMatcherBuilder, PropertyValueMatcherBuilder,
        DependencyNameExtractorMatcherBuilder, FinalMatcherBuilder {

    private MissionHealthCheckMatcher.MatchCriteria criteria;
    private String regex;
    private String name;
    private DependencyNameExtractor extractor;
    private Set<MissionHealthCheckMatcher> operands;
    private MissionHealthCheckMatcher operand;

    /**
     * Method starting the builder chain.
     *
     * @return builder
     */
    public static InitialMissionHealthCheckMatcherBuilder builder() {
        return new MissionHealthCheckMatcherBuilder();
    }

    @Override
    public FinalMatcherBuilder classNamePattern(final String regex) {
        this.criteria = MissionHealthCheckMatcher.MatchCriteria.CLASS;
        this.regex = Objects.requireNonNull(regex);
        return this;
    }

    @Override
    public FinalMatcherBuilder dependency(final String name) {
        this.criteria = MissionHealthCheckMatcher.MatchCriteria.DEPENDENCY;
        this.name = Objects.requireNonNull(name);
        return this;
    }

    @Override
    public DependencyNameExtractorMatcherBuilder dependencyWith(final String name) {
        dependency(name);
        return this;
    }

    @Override
    public FinalMatcherBuilder extractor(final DependencyNameExtractor extractor) {
        this.extractor = extractor;
        return this;
    }

    @Override
    public PropertyValueMatcherBuilder envVariable(final String name) {
        this.criteria = MissionHealthCheckMatcher.MatchCriteria.ENVIRONMENT;
        this.name = Objects.requireNonNull(name);
        return this;
    }

    @Override
    public PropertyValueMatcherBuilder property(final String name) {
        this.criteria = MissionHealthCheckMatcher.MatchCriteria.PROPERTY;
        this.name = Objects.requireNonNull(name);
        return this;
    }

    @Override
    public FinalMatcherBuilder valuePattern(final String regex) {
        this.regex = Objects.requireNonNull(regex);
        return this;
    }

    @Override
    public AndMatcherBuilder and(final MissionHealthCheckMatcher matcher) {
        andAtLast(matcher);
        return this;
    }

    @Override
    public FinalMatcherBuilder andAtLast(final MissionHealthCheckMatcher matcher) {
        if (operands == null) {
            this.criteria = MissionHealthCheckMatcher.MatchCriteria.AND;
            operands = new TreeSet<>();
        }
        operands.add(Objects.requireNonNull(matcher, "matcher cannot be null."));
        return this;
    }

    @Override
    public FinalMatcherBuilder not(final MissionHealthCheckMatcher matcher) {
        this.criteria = MissionHealthCheckMatcher.MatchCriteria.NOT;
        this.operand = Objects.requireNonNull(matcher, "matcher cannot be null.");
        return this;
    }

    @Override
    public OrMatcherBuilder or(final MissionHealthCheckMatcher matcher) {
        orAtLast(matcher);
        return this;
    }

    @Override
    public FinalMatcherBuilder orAtLast(final MissionHealthCheckMatcher matcher) {
        if (operands == null) {
            this.criteria = MissionHealthCheckMatcher.MatchCriteria.OR;
            operands = new TreeSet<>();
        }
        operands.add(Objects.requireNonNull(matcher, "matcher cannot be null."));
        return this;
    }

    @Override
    public MissionHealthCheckMatcher build() {
        switch (Objects.requireNonNull(criteria, "Builder not initialized.")) {
            case AND:
                return new AndMatcher(operands);
            case OR:
                return new OrMatcher(operands);
            case NOT:
                return new NotMatcher(operand);
            case DEPENDENCY:
                return new DependencyMatcher(name, extractor);
            case CLASS:
                return new ClassMatcher(regex);
            case ENVIRONMENT:
                return new EnvironmentMatcher(name, regex);
            case PROPERTY:
                return new SystemPropertyMatcher(name, regex);
            default:
                throw new UnsupportedOperationException("Unsupported match criteria found: " + criteria);
        }
    }
}
