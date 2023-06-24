package com.github.nagyesta.abortmission.booster.junit4;

import com.github.nagyesta.abortmission.core.MissionControl;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.springframework.beans.factory.UnsatisfiedDependencyException;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.testkit.LaunchEvaluationUtil.*;
import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;

public class StaticFireBoosterTest {

    private static final List<String> EXPECTED_DISPLAY_NAMES_COUNTDOWN = Stream.<Stream<String>>builder()
            .add(Stream.of("StaticFireTestWithSideBoosters"))
            .add(staticFireTestInputProvider().mapToObj(i -> "StaticFireTestWithSideBoostersParametrized"))
            .build()
            .flatMap(Function.identity())
            .collect(Collectors.toList());
    private static final List<String> EXPECTED_DISPLAY_NAMES = List.of("testIsOnFire");
    private static final String EXCEPTION = UnsatisfiedDependencyException.class.getName();
    private static final String MESSAGE_FORMAT = "Error creating bean with name 'com.github.nagyesta.abortmission.booster.junit4."
            + "%s': Unsatisfied dependency expressed through field 'sideBooster'; nested exception "
            + "is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'sideBooster' defined "
            + "in com.github.nagyesta.abortmission.testkit.spring.StaticFire: Bean instantiation via factory method failed; nested "
            + "exception is org.springframework.beans.BeanInstantiationException: Failed to instantiate [com.github.nagyesta."
            + "abortmission.testkit.spring.Booster]: Factory method 'sideBooster' threw exception; nested exception is java.lang."
            + "UnsupportedOperationException: Side boosters are not supported.";

    private static final List<String> MESSAGES = Stream
            .of("StaticFireTestWithSideBoosters", "StaticFireTestWithSideBoostersParametrized")
            .map(s -> String.format(MESSAGE_FORMAT, s))
            .collect(Collectors.toList());

    @Test
    @Tag("integration")
    public void testAssumption() throws NoSuchMethodException {
        EngineTestKit
                .engine("junit-vintage")
                .selectors(selectClass(StaticFireTestCenterCoreOnly.class),
                        selectClass(StaticFireTestWithSideBoosters.class),
                        selectClass(StaticFireTestWithSideBoostersParametrized.class))
                .execute()
                .testEvents()
                .assertStatistics(stats -> stats
                        .skipped(DISABLED_CASES)
                        .started(TOTAL_CASES)
                        .succeeded(SUCCESSFUL_CASES)
                        .aborted(ABORTED_CASES)
                        .failed(FAILED_CASES));
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestWithSideBoosters.class)
                .forEach(evaluator -> {
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(SIDE_BOOSTER_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check display names
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES_COUNTDOWN, findCountdownDisplayNamesForMeasurementsOf(evaluator));
                    assertIterableEquals(List.of(), findMissionDisplayNamesForMeasurementsOf(evaluator));
                    //check exception details
                    assertIterableEquals(List.of(), findExceptionsForMissionFailuresOf(evaluator));
                    assertIterableEquals(List.of(), findThrowableMessagesForMissionFailuresOf(evaluator));
                    assertIterableEquals(List.of(EXCEPTION, EXCEPTION), findExceptionsForCountdownFailuresOf(evaluator));
                    assertIterableEquals(MESSAGES, findThrowableMessagesForCountdownFailuresOf(evaluator));
                });
        MissionControl.matchingHealthChecks(STATIC_FIRE, StaticFireTestCenterCoreOnly.class.getDeclaredMethod("testIsOnFire"))
                .forEach(evaluator -> {
                    assertEquals(CENTER_CORE_NOMINAL_STATS.getReadOnlyCountdown().getSnapshot(),
                            evaluator.getStats().getReadOnlyCountdown().getSnapshot());
                    assertEquals(CENTER_CORE_NOMINAL_STATS.getReadOnlyMission().getSnapshot(),
                            evaluator.getStats().getReadOnlyMission().getSnapshot());
                    //check display names
                    assertIterableEquals(List.of(), findCountdownDisplayNamesForMeasurementsOf(evaluator));
                    assertIterableEquals(EXPECTED_DISPLAY_NAMES, findMissionDisplayNamesForMeasurementsOf(evaluator));
                    //check exception details
                    assertIterableEquals(List.of(), findExceptionsForMissionFailuresOf(evaluator));
                    assertIterableEquals(List.of(), findThrowableMessagesForMissionFailuresOf(evaluator));
                });
    }

    public interface SideBooster {
    }

    public interface CenterCore {
    }

    public interface StaticFire {
    }

    public interface Booster {
    }

}
