package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings({"java:S3577", "NewClassNamingConvention"})
//we want to avoid the default names to pick up only that class which we want
@LaunchAbortArmed(STATIC_FIRE)
@SpringBootTest(classes = StaticFire.class)
@Tag(SIDE_BOOSTER)
class StaticFireTestWithSideBoosters {

    @Autowired
    private Booster centerCore;
    @Autowired
    private Booster sideBooster;

    private static Stream<Arguments> attemptIndexProvider() {
        return StaticFireTestAssets.staticFireTestInputProvider().mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("attemptIndexProvider")
    void testIsOnFire(final int ignore) {
        //given

        //when
        final var actual = sideBooster.isOnFire();

        //then
        assertTrue(actual);
    }

    @Test
    @SuppressAbortDecisions
    @Tag(BOOSTER)
    @Tag(CENTER_CORE)
    void testIsOnFireNoAbort() {
        //given

        //when
        final var actual = centerCore.isOnFire();

        //then
        assertTrue(actual);
    }
}
