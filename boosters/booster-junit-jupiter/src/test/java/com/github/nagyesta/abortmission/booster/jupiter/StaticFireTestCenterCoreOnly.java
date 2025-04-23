package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.annotation.LaunchSequence;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"java:S3577", "NewClassNamingConvention"})
//we want to avoid the default names to pick up only that class which we want
@SuppressAbortDecisions
@LaunchAbortArmed(STATIC_FIRE)
@SpringBootTest(classes = StaticFire.class)
@LaunchSequence(MissionOutlineDefinition.class)
class StaticFireTestCenterCoreOnly {

    @Autowired
    private Booster centerCore;

    @Test
    @Tag(BOOSTER)
    @Tag(CENTER_CORE)
    void testIsOnFire() {
        //given

        //when
        final var actual = centerCore.isOnFire();

        //then
        assertTrue(actual);
    }

    @Test
    void testIsOnFireNotMatchingRules() {
        //given

        //when
        final var actual = centerCore.isOnFire();

        //then
        assertTrue(actual);
    }
}
