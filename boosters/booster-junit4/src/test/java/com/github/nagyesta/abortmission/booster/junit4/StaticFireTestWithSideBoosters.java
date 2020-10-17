package com.github.nagyesta.abortmission.booster.junit4;

import com.github.nagyesta.abortmission.booster.junit4.annotation.LaunchAbortArmed;
import com.github.nagyesta.abortmission.booster.junit4.support.LaunchAbortTestWatcher;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestContextManager;

import static com.github.nagyesta.abortmission.testkit.spring.StaticFireTestAssets.STATIC_FIRE;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("checkstyle:JavadocVariable")
@LaunchAbortArmed(STATIC_FIRE)
@SpringBootTest(classes = StaticFire.class)
@SuppressAbortDecisions
@Category(StaticFireBoosterTest.SideBooster.class)
public class StaticFireTestWithSideBoosters {

    @Rule
    public LaunchAbortTestWatcher watcher = new LaunchAbortTestWatcher(this.getClass());
    @Autowired
    private Booster centerCore;
    @Autowired
    private Booster sideBooster;
    private TestContextManager testContextManager;


    @Before
    public final void setUp() throws Exception {
        if (testContextManager == null) {
            testContextManager = new TestContextManager(this.getClass());
        }
        testContextManager.beforeTestClass();
        testContextManager.getTestContext().getApplicationContext().getAutowireCapableBeanFactory().autowireBean(this);
    }

    @Test
    @Category({StaticFireBoosterTest.Booster.class, StaticFireBoosterTest.CenterCore.class})
    public void testIsOnFireNoAbort() {
        //given

        //when
        final boolean actual = centerCore.isOnFire();

        //then
        assertTrue(actual);
    }
}
