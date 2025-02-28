package com.github.nagyesta.abortmission.booster.junit4;

import com.github.nagyesta.abortmission.booster.junit4.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.junit4.support.LaunchAbortTestWatcher;
import com.github.nagyesta.abortmission.core.annotation.LaunchSequence;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.STATIC_FIRE;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("checkstyle:JavadocVariable")
@SuppressAbortDecisions
@LaunchAbortArmed(STATIC_FIRE)
@RunWith(SpringRunner.class)
@SpringBootTest(classes = StaticFire.class)
@LaunchSequence(MissionOutlineDefinition.class)
public class StaticFireTestCenterCoreOnly {

    @Rule
    public LaunchAbortTestWatcher watcher = new LaunchAbortTestWatcher(this.getClass());

    @Autowired
    private Booster centerCore;

    @Test
    @Category({StaticFireBoosterTest.Booster.class, StaticFireBoosterTest.CenterCore.class})
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
