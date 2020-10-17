package com.github.nagyesta.abortmission.booster.junit4;

import com.github.nagyesta.abortmission.booster.junit4.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.junit4.support.LaunchAbortTestWatcher;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestContextManager;

import java.util.List;
import java.util.stream.Collectors;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.STATIC_FIRE;
import static org.junit.Assert.assertTrue;

@SuppressWarnings({"checkstyle:JavadocVariable", "checkstyle:VisibilityModifier"})
@LaunchAbortArmed(STATIC_FIRE)
@RunWith(Parameterized.class)
@SpringBootTest(classes = StaticFire.class)
@Category(StaticFireBoosterTest.SideBooster.class)
public class StaticFireTestWithSideBoostersParametrized {

    @Autowired
    private static Booster centerCore;
    @Autowired
    private Booster sideBooster;
    private TestContextManager testContextManager;
    @Parameterized.Parameter
    public int parameter;
    @Rule
    public LaunchAbortTestWatcher watcher = new LaunchAbortTestWatcher(this.getClass());

    @Parameterized.Parameters
    public static List<Integer> attemptIndexProvider() {
        return StaticFireTestAssets.staticFireTestInputProvider().boxed().collect(Collectors.toList());
    }

    @Before
    public final void setUp() throws Exception {
        if (testContextManager == null) {
            testContextManager = new TestContextManager(this.getClass());
        }
        testContextManager.beforeTestClass();
        testContextManager.getTestContext().getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Test
    @Category({StaticFireBoosterTest.StaticFire.class, StaticFireBoosterTest.SideBooster.class})
    public void testIsOnFire() {
        //given

        //when
        final boolean actual = sideBooster.isOnFire();

        //then
        assertTrue(actual);
    }
}
