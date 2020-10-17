package com.github.nagyesta.abortmission.core.annotation;

import com.github.nagyesta.abortmission.core.outline.MissionOutline;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Helper class to evaluate annotated elements during the primary mission of the library.
 */
public final class AnnotationContextEvaluator {

    private static final String DEFAULT_MISSION_OUTLINE_DEFINITION_CLASS_NAME = "MissionOutlineDefinition";
    private static final String ROOT_PACKAGE = "";
    private static final String REGEX_DOT = "\\.";

    /**
     * Returns the shared evaluator instance.
     *
     * @return the shared instance.
     */
    public static AnnotationContextEvaluator shared() {
        return AnnotationContextEvaluatorHolder.INSTANCE;
    }

    /**
     * Finds the context name using a booster specific annotation based on a test method.
     *
     * @param activeMethod         The test method we would like to execute.
     * @param annotation           The booster specific annotation class.
     * @param contextNameExtractor A function that can extract the name from the annotation.
     * @param <A>                  The annotation type.
     * @return An optional context name. If empty, the shared context will be used.
     */
    public <A extends Annotation> Optional<String> findContextName(final Method activeMethod,
                                                                   final Class<A> annotation,
                                                                   final Function<A, String> contextNameExtractor) {
        final Class<?> activeClass = activeMethod.getDeclaringClass();
        return findContextName(activeClass, annotation, contextNameExtractor);
    }

    /**
     * Finds the context name using a booster specific annotation based on a test class.
     *
     * @param activeClass          The test class we would like to execute.
     * @param annotation           The booster specific annotation class.
     * @param contextNameExtractor A function that can extract the name from the annotation.
     * @param <A>                  The annotation type.
     * @return An optional context name. If empty, the shared context will be used.
     */
    public <A extends Annotation> Optional<String> findContextName(final Class<?> activeClass,
                                                                   final Class<A> annotation,
                                                                   final Function<A, String> contextNameExtractor) {
        Optional<String> result = Optional.empty();
        if (activeClass.isAnnotationPresent(annotation)) {
            final String contextName = contextNameExtractor.apply(activeClass.getAnnotation(annotation));
            if (contextName != null && !contextName.trim().isEmpty()) {
                result = Optional.of(contextName.trim());
            }
        }
        return result;
    }

    /**
     * Returns whether abort decisions should be overruled/suppressed in case of the test method.
     *
     * @param activeMethod The test method.
     * @return true if the method or the declaring class is annotated with {@link SuppressAbortDecisions}, false otherwise.
     */
    public boolean isAbortSuppressed(final Method activeMethod) {
        final Class<?> activeClass = activeMethod.getDeclaringClass();
        final boolean suppressOnMethod = activeMethod.isAnnotationPresent(SuppressAbortDecisions.class);
        final boolean suppressOnClass = activeClass.isAnnotationPresent(SuppressAbortDecisions.class);
        return suppressOnClass || suppressOnMethod;
    }

    /**
     * Returns whether abort decisions should be overruled/suppressed in case of the test class.
     *
     * @param activeClass The test class.
     * @return true if the class is annotated with {@link SuppressAbortDecisions}, false otherwise.
     */
    public boolean isAbortSuppressed(final Class<?> activeClass) {
        return activeClass.isAnnotationPresent(SuppressAbortDecisions.class);
    }

    /**
     * Finds the list of exceptions we consider as allowed to slip through undetected using {@link SuppressLaunchFailureReporting}.
     * These exceptions won't increase failure counts in our statistics.
     *
     * @param testInstanceClass The test class.
     * @return the set of ignored exceptions.
     */
    public Set<Class<? extends Exception>> findSuppressedExceptions(final Class<?> testInstanceClass) {
        final boolean suppressOnClass = testInstanceClass.isAnnotationPresent(SuppressLaunchFailureReporting.class);
        final Set<Class<? extends Exception>> suppressedExceptions = new HashSet<>();
        if (suppressOnClass) {
            final SuppressLaunchFailureReporting annotation = testInstanceClass.getAnnotation(SuppressLaunchFailureReporting.class);
            suppressedExceptions.addAll(Arrays.asList(annotation.forExceptions()));
        }
        return suppressedExceptions;
    }

    /**
     * Finds the list of exceptions we consider as allowed to slip through undetected using {@link SuppressLaunchFailureReporting}.
     * These exceptions won't increase failure counts in our statistics.
     * <p>
     * If the annotation is present both on the method and the class, the sets will be merged using a union operation.
     *
     * @param activeMethod The test class.
     * @return the set of ignored exceptions.
     */
    public Set<Class<? extends Exception>> findSuppressedExceptions(final Method activeMethod) {
        final Set<Class<? extends Exception>> suppressedExceptions = findSuppressedExceptions(activeMethod.getDeclaringClass());
        final boolean suppressOnMethod = activeMethod.isAnnotationPresent(SuppressLaunchFailureReporting.class);
        if (suppressOnMethod) {
            final SuppressLaunchFailureReporting annotation = activeMethod.getAnnotation(SuppressLaunchFailureReporting.class);
            suppressedExceptions.addAll(Arrays.asList(annotation.forExceptions()));
        }
        return suppressedExceptions;
    }

    /**
     * Searches for a {@link LaunchSequence} in order to find the relevant mission context initializer class and initializes
     * the context if it is not yet done by other classes.
     *
     * @param testInstanceClass The test class.
     */
    public void findAndApplyLaunchPlanDefinition(final Class<?> testInstanceClass) {
        if (Objects.requireNonNull(testInstanceClass, "Test instance cannot be null.")
                .isAnnotationPresent(LaunchSequence.class)) {
            final LaunchSequence annotation = testInstanceClass.getAnnotation(LaunchSequence.class);
            final Class<? extends MissionOutline> missionOutlineClass = annotation.value();
            doInitialBriefingOfMissionOutline(missionOutlineClass);
        } else {
            final Optional<Class<? extends MissionOutline>> closestMatch = findClosestDefaultMissionOutline(testInstanceClass);
            if (closestMatch.isPresent()) {
                doInitialBriefingOfMissionOutline(closestMatch.get());
            } else if (hasDefaultMissionOutlineClass(ROOT_PACKAGE)) {
                doInitialBriefingOfMissionOutline(getDefaultMissionOutlineClass(ROOT_PACKAGE));
            }
        }
    }

    private Optional<Class<? extends MissionOutline>> findClosestDefaultMissionOutline(final Class<?> testInstanceClass) {
        final String[] split = testInstanceClass.getName().split(REGEX_DOT);
        return IntStream.range(1, split.length)
                .mapToObj(excludeLast -> Arrays
                        .stream(split)
                        .limit(split.length - excludeLast)
                        .collect(Collectors.joining(".")) + ".")
                .filter(this::hasDefaultMissionOutlineClass)
                .map(this::getDefaultMissionOutlineClass)
                .findFirst()
                .map(c -> (Class<? extends MissionOutline>) c);
    }

    @SuppressWarnings("unchecked")
    private Class<? extends MissionOutline> getDefaultMissionOutlineClass(final String packageName) {
        try {
            return (Class<? extends MissionOutline>) Class.forName(packageName + DEFAULT_MISSION_OUTLINE_DEFINITION_CLASS_NAME);
        } catch (final ClassNotFoundException ignore) {
            return null;
        }
    }

    private boolean hasDefaultMissionOutlineClass(final String packageName) {
        try {
            MissionOutline.class.isAssignableFrom(Class.forName(packageName + DEFAULT_MISSION_OUTLINE_DEFINITION_CLASS_NAME));
            return true;
        } catch (final ClassNotFoundException e) {
            return false;
        }
    }

    private void doInitialBriefingOfMissionOutline(final Class<? extends MissionOutline> missionOutlineClass) {
        try {
            final MissionOutline outline = missionOutlineClass.getConstructor().newInstance();
            outline.initialBriefing();
        } catch (final Exception e) {
            throw new IllegalArgumentException("Mission outline cannot be instantiated." + e.getMessage(), e);
        }
    }

    private static final class AnnotationContextEvaluatorHolder {
        private static final AnnotationContextEvaluator INSTANCE = new AnnotationContextEvaluator();
    }
}
