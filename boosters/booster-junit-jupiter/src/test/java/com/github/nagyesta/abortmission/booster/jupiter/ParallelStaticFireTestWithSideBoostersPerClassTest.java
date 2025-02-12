package com.github.nagyesta.abortmission.booster.jupiter;

import com.github.nagyesta.abortmission.booster.jupiter.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;
import java.util.stream.Stream;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.PARALLEL;
import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.PER_CLASS;
import static org.junit.jupiter.api.Assertions.assertTrue;

@LaunchAbortArmed(PARALLEL)
@SpringBootTest(classes = StaticFire.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag(PER_CLASS)
public class ParallelStaticFireTestWithSideBoostersPerClassTest {

    private static final Random RANDOM = new Random();
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelStaticFireTestWithSideBoostersPerClassTest.class);

    @Autowired
    private Booster centerCore;

    private static Stream<Arguments> attemptIndexProvider() {
        return StaticFireTestAssets.staticFireTestParallelInputProvider().mapToObj(Arguments::of);
    }

    @ParameterizedTest
    @MethodSource("attemptIndexProvider")
    public void testIsOnFire0(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @ParameterizedTest
    @MethodSource("attemptIndexProvider")
    public void testIsOnFire1(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @ParameterizedTest
    @MethodSource("attemptIndexProvider")
    public void testIsOnFire2(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @ParameterizedTest
    @MethodSource("attemptIndexProvider")
    public void testIsOnFire3(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    private void executeTest(final int wait) throws InterruptedException {
        //given
        ThreadTracker.THREADS_USED.add(Thread.currentThread().getName());
        LOGGER.info("Running test with max wait: {}", wait);
        Thread.sleep(RANDOM.nextInt(wait));

        //when
        final boolean actual = centerCore.isOnFire();

        //then
        assertTrue(actual);
    }
}
