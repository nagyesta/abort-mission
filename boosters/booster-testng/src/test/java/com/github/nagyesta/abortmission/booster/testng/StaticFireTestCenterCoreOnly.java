package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.testng.listener.AbortMissionListener;
import com.github.nagyesta.abortmission.core.annotation.LaunchSequence;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.testng.Assert.assertTrue;

@SuppressWarnings({"java:S3577", "NewClassNamingConvention"})
//we want to avoid the default names to pick up only that class which we want
@SuppressAbortDecisions
@LaunchAbortArmed(STATIC_FIRE)
@Listeners(AbortMissionListener.class)
@SpringBootTest(classes = StaticFire.class)
@LaunchSequence(MissionOutlineDefinition.class)
public class StaticFireTestCenterCoreOnly extends AbstractTestNGSpringContextTests {

    @Autowired
    private Booster centerCore;

    @Test(groups = {BOOSTER, CENTER_CORE})
    public void testIsOnFire() {
        //given

        //when
        final var actual = centerCore.isOnFire();

        //then
        assertTrue(actual);
    }

    @Test
    public void testIsOnFireNotMatchingRules() {
        //given

        //when
        final var actual = centerCore.isOnFire();

        //then
        assertTrue(actual);
    }
}
