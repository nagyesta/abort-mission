package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.jupiter.extractor.TagDependencyNameExtractor;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.testkit.vanilla.WeatherTestAssets.*;

@SuppressWarnings({"java:S3577", "NewClassNamingConvention"})
//we want to avoid the default names to pick up only that class which we want
@LaunchAbortArmed(CONTEXT_NAME)
@TestMethodOrder(MethodOrderer.MethodName.class)
class WeatherTestContext {

    private static final int THRESHOLD = 50;

    static {
        new MissionOutline() {
            @Override
            protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
                return getMissionPlan(new TagDependencyNameExtractor());
            }
        }.initialBriefing();
    }

    private static Stream<Arguments> weatherInputProvider() {
        return weatherTestInputProvider().mapToObj(Arguments::of);
    }

    @SuppressWarnings("java:S2699")
    @ParameterizedTest
    @MethodSource("weatherInputProvider")
    @Tag("weather")
    void testLaunchShouldBeDelayedWhenAStormIsLikely(final int stormChance) {
        Assertions.assertTrue(stormChance < THRESHOLD, "Something is wrong.");
    }

    @Test
    @Tag("launch")
    void testLaunchShouldIgnoreWeatherPrediction() {
        Assertions.assertTrue(true);
    }

    @Test
    @Tag("launch")
    void testLaunchShouldNotCheckWeather() {
        Assertions.assertTrue(true);
    }
}
