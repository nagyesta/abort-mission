package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.annotation.LaunchSequence;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressAbortDecisions
@LaunchAbortArmed(STATIC_FIRE)
@SpringBootTest(classes = StaticFire.class)
@LaunchSequence(MissionOutlineDefinition.class)
public class StaticFireTestCenterCoreOnly {

    @Autowired
    private Booster centerCore;

    @Test
    @Tags({@Tag(BOOSTER), @Tag(CENTER_CORE)})
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
