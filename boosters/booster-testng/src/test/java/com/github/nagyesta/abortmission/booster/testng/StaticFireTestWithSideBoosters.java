package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.testng.listener.AbortMissionListener;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.testng.Assert.assertTrue;

@SuppressWarnings({"java:S3577", "NewClassNamingConvention"})
//we want to avoid the default names to pick up only that class which we want
@LaunchAbortArmed(STATIC_FIRE)
@Listeners(AbortMissionListener.class)
@SpringBootTest(classes = StaticFire.class)
@Test(groups = SIDE_BOOSTER)
public class StaticFireTestWithSideBoosters extends AbstractTestNGSpringContextTests {

    @Autowired
    private Booster centerCore;
    @Autowired
    private Booster sideBooster;

    @DataProvider(name = "attemptIndexProvider")
    private static Object[][] attemptIndexProvider() {
        return StaticFireTestAssets.staticFireTestInputProvider().boxed()
                .map(List::of)
                .map(List::toArray)
                .toList()
                .toArray(new Object[(int) StaticFireTestAssets.staticFireTestInputProvider().count()][1]);
    }

    @Test(dataProvider = "attemptIndexProvider")
    public void testIsOnFire(final int ignore) {
        //given

        //when
        final var actual = sideBooster.isOnFire();

        //then
        assertTrue(actual);
    }

    @Test(groups = {BOOSTER, CENTER_CORE})
    @SuppressAbortDecisions
    public void testIsOnFireNoAbort() {
        //given

        //when
        final var actual = centerCore.isOnFire();

        //then
        assertTrue(actual);
    }
}
