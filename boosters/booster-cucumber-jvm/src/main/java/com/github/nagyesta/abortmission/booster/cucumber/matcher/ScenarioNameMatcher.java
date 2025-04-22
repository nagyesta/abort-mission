package com.github.nagyesta.abortmission.booster.cucumber.matcher;

import com.github.nagyesta.abortmission.core.matcher.impl.CustomMatcher;
import io.cucumber.java.Scenario;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * A {@link com.github.nagyesta.abortmission.core.matcher.MissionHealthCheckMatcher} implementation
 * for regex matching of Scenario names.
 */
@SuppressWarnings("java:S2160") //the parent implements equals and hashcode
public class ScenarioNameMatcher extends CustomMatcher {

    private final String pattern;
    private final Pattern compiledPattern;

    /**
     * Constructor validating and setting the regular expression for the scenario name matching.
     *
     * @param pattern The regular expression.
     */
    public ScenarioNameMatcher(final String pattern) {
        this.pattern = Objects.requireNonNull(pattern, "Scenario name regex cannot be null.");
        this.compiledPattern = Pattern.compile(pattern);
    }

    /**
     * Returns the regular expression held by this instance.
     *
     * @return The regular expression
     */
    public final String getPattern() {
        return pattern;
    }

    @Override
    public String getName() {
        return "SCENARIO_NAME_MATCHING('" + pattern + "')";
    }

    @Override
    public boolean matches(final Object component) {
        if (!(component instanceof Scenario)) {
            return false;
        }
        return compiledPattern.matcher(Objects.requireNonNull(convert((Scenario) component))).matches();
    }

    private String convert(final Scenario component) {
        return component.getName();
    }

}
