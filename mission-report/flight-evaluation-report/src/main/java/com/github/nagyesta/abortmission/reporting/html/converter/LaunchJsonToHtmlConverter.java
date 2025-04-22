package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.TestRunHtml;
import com.github.nagyesta.abortmission.reporting.json.ClassJson;
import com.github.nagyesta.abortmission.reporting.json.LaunchJson;
import com.github.nagyesta.abortmission.reporting.json.StageLaunchStatsJson;
import com.github.nagyesta.abortmission.reporting.json.TestRunJson;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class LaunchJsonToHtmlConverter {
    private static final int HASH_RADIX = 16;
    private static final int HASH_LENGTH = 12;
    private static final LaunchHtml EMPTY = LaunchHtml.builder()
            .matchers(Collections.emptyMap())
            .classNames(Collections.emptyMap())
            .methodNames(Collections.emptyMap())
            .runs(Collections.emptySortedSet())
            .build();

    /**
     * Converts the JSON representation of a test run telemetry to the HTML representation.
     *
     * @param source The JSON representation of the test run telemetry.
     * @return The HTML representation.
     */
    public LaunchHtml apply(final LaunchJson source) {
        return source.getClasses().entrySet().stream()
                .map(this::convertClass)
                .reduce(this::merge)
                .orElse(EMPTY);
    }

    private LaunchHtml convertClass(final Map.Entry<String, ClassJson> classEntry) {
        final var countdown = convertCountdown(classEntry.getKey(), classEntry.getValue());
        return classEntry.getValue().getLaunches().entrySet().stream()
                .map(methodEntry -> convertMission(classEntry.getKey(), methodEntry.getKey(), methodEntry.getValue()))
                .reduce(countdown, this::merge);
    }

    private LaunchHtml convertMission(final String className, final String methodName, final StageLaunchStatsJson methodData) {
        final var classKey = shortHash(className);
        final var methodKey = shortHash(methodName);
        final var methodMatchers = methodData.getMatcherNames()
                .stream()
                .collect(Collectors.toMap(this::shortHash, Function.identity()));
        final var launchMeasurements = methodData.getTimeMeasurements().stream()
                .map(convertMissionMeasurement(classKey, methodKey, methodMatchers))
                .collect(Collectors.toCollection(TreeSet::new));
        return LaunchHtml.builder()
                .matchers(methodMatchers)
                .classNames(Collections.singletonMap(classKey, className))
                .methodNames(Collections.singletonMap(methodKey, methodName))
                .runs(launchMeasurements)
                .build();
    }

    private LaunchHtml convertCountdown(final String className, final ClassJson classData) {
        final var classKey = shortHash(className);
        final var classMatchers = classData.getCountdown().getMatcherNames()
                .stream()
                .collect(Collectors.toMap(this::shortHash, Function.identity()));
        final SortedSet<TestRunHtml> countDownMeasurements = classData.getCountdown().getTimeMeasurements().stream()
                .map(convertCountdownMeasurement(classKey, classMatchers))
                .collect(Collectors.toCollection(TreeSet::new));
        return LaunchHtml.builder()
                .matchers(classMatchers)
                .classNames(Collections.singletonMap(classKey, className))
                .methodNames(Collections.emptyMap())
                .runs(countDownMeasurements)
                .build();
    }

    private LaunchHtml merge(final LaunchHtml a, final LaunchHtml b) {
        return LaunchHtml.builder()
                .matchers(mergeMaps(a.getMatchers(), b.getMatchers()))
                .classNames(mergeMaps(a.getClassNames(), b.getClassNames()))
                .methodNames(mergeMaps(a.getMethodNames(), b.getMethodNames()))
                .runs(mergeSets(a.getRuns(), b.getRuns()))
                .build();
    }

    private static Map<String, String> mergeMaps(final Map<String, String> a, final Map<String, String> b) {
        return Stream.concat(a.entrySet().stream(), b.entrySet().stream())
                .distinct()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private static SortedSet<TestRunHtml> mergeSets(final Set<TestRunHtml> a, final Set<TestRunHtml> b) {
        return Stream.concat(a.stream(), b.stream())
                .collect(Collectors.toCollection(TreeSet::new));
    }

    private static Function<TestRunJson, TestRunHtml> convertCountdownMeasurement(
            final String classKey, final Map<String, String> classMatchers) {
        return testRunJson -> convertCommonMeasurementFields(classKey, classMatchers, testRunJson)
                .methodKey(null)
                .countdown(true)
                .build();
    }

    private static Function<TestRunJson, TestRunHtml> convertMissionMeasurement(
            final String classKey, final String methodKey, final Map<String, String> methodMatchers) {
        return testRunJson -> convertCommonMeasurementFields(classKey, methodMatchers, testRunJson)
                .methodKey(methodKey)
                .countdown(false)
                .build();
    }

    private static TestRunHtml.TestRunHtmlBuilder convertCommonMeasurementFields(
            final String classKey, final Map<String, String> matchers, final TestRunJson testRunJson) {
        return TestRunHtml.builder()
                .classKey(classKey)
                .matcherKeys(matchers.keySet())
                .result(testRunJson.getResult())
                .start(testRunJson.getStart())
                .end(testRunJson.getEnd())
                .threadName(testRunJson.getThreadName())
                .launchId(testRunJson.getLaunchId())
                .displayName(testRunJson.getDisplayName())
                .throwableClass(testRunJson.getThrowableClass())
                .throwableMessage(testRunJson.getThrowableMessage())
                .stackTrace(testRunJson.getStackTrace());
    }

    /**
     * Hashes and shortens a given string (meant to be used for class/method ID generation).
     *
     * @param displayName The display name of the item we need to hash.
     * @return The hashed and shortened value.
     */
    protected String shortHash(final String displayName) {
        final var hash = new BigInteger(sha1(displayName)).abs().toString(HASH_RADIX);
        return hash.substring(0, Math.min(hash.length(), HASH_LENGTH));
    }

    @SuppressWarnings("java:S4790") //the hash algorithm is not used for security
    private static byte[] sha1(final String message) {
        try {
            return MessageDigest.getInstance("SHA-1").digest(message.getBytes());
        } catch (final NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("SHA-1 is not supported on this platform.");
        }
    }
}
