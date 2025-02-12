package com.github.nagyesta.abortmission.booster.testng;

import com.github.nagyesta.abortmission.booster.testng.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.testng.listener.AbortMissionListener;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.PARALLEL;
import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.SIDE_BOOSTER;
import static org.testng.AssertJUnit.assertTrue;

@LaunchAbortArmed(PARALLEL)
@Listeners(AbortMissionListener.class)
@SpringBootTest(classes = StaticFire.class)
@Test(groups = SIDE_BOOSTER)
public class ParallelStaticFireTestWithSideBoosters extends AbstractTestNGSpringContextTests {

    static final CopyOnWriteArraySet<String> THREADS_USED = new CopyOnWriteArraySet<>();
    private static final Random RANDOM = new Random();
    private static final Logger LOGGER = LoggerFactory.getLogger(ParallelStaticFireTestWithSideBoosters.class);

    @Autowired
    private Booster centerCore;

    @DataProvider(name = "attemptIndexProvider")
    private static Object[][] attemptIndexProvider() {
        return StaticFireTestAssets.staticFireTestParallelInputProvider().boxed()
                .map(List::of)
                .map(List::toArray)
                .collect(Collectors.toList())
                .toArray(new Object[(int) StaticFireTestAssets.staticFireTestParallelInputProvider().count()][1]);
    }


    @Test(dataProvider = "attemptIndexProvider")
    public void testIsOnFire0(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @Test(dataProvider = "attemptIndexProvider")
    public void testIsOnFire1(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @Test(dataProvider = "attemptIndexProvider")
    public void testIsOnFire2(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @Test(dataProvider = "attemptIndexProvider")
    public void testIsOnFire3(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @Test(dataProvider = "attemptIndexProvider")
    public void testIsOnFire4(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @Test(dataProvider = "attemptIndexProvider")
    public void testIsOnFire5(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @Test(dataProvider = "attemptIndexProvider")
    public void testIsOnFire6(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    @Test(dataProvider = "attemptIndexProvider")
    public void testIsOnFire7(final int wait) throws InterruptedException {
        executeTest(wait);
    }

    private void executeTest(final int wait) throws InterruptedException {
        //given
        THREADS_USED.add(Thread.currentThread().getName());
        LOGGER.info("Running test with max wait: {}", wait);
        Thread.sleep(RANDOM.nextInt(wait));

        //when
        final boolean actual = centerCore.isOnFire();

        //then
        assertTrue(actual);
    }
}
