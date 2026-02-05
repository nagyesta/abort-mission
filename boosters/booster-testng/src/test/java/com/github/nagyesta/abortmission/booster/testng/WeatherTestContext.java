package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.testng.extractor.GroupDependencyNameExtractor;
import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.outline.MissionOutline;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.github.nagyesta.abortmission.testkit.vanilla.WeatherTestAssets.*;
import static org.testng.Assert.assertTrue;

@SuppressWarnings({"java:S3577", "NewClassNamingConvention"})
//we want to avoid the default names to pick up only that class which we want
@LaunchAbortArmed(CONTEXT_NAME)
class WeatherTestContext {

    private static final int THRESHOLD = 50;

    static {
        new MissionOutline() {
            @Override
            protected Map<String, Consumer<AbortMissionCommandOps>> defineOutline() {
                return getMissionPlan(new GroupDependencyNameExtractor());
            }
        }.initialBriefing();
    }


    @DataProvider(name = "weatherInputProvider")
    private static Object[][] weatherInputProvider() {
        return weatherTestInputProvider().boxed()
                .map(List::of)
                .map(List::toArray)
                .toList()
                .toArray(new Object[(int) weatherTestInputProvider().count()][1]);
    }

    @SuppressWarnings("java:S2699")
    @Test(dataProvider = "weatherInputProvider", groups = "weather")
    void testLaunchShouldBeDelayedWhenAStormIsLikely(final int stormChance) {
        assertTrue(stormChance < THRESHOLD, "Something is wrong.");
    }

    @Test(groups = "launch", dependsOnGroups = "weather")
    void testLaunchShouldIgnoreWeatherPrediction() {
        assertTrue(true);
    }

    @Test(groups = "launch", dependsOnGroups = "weather")
    void testLaunchShouldNotCheckWeather() {
        assertTrue(true);
    }
}
