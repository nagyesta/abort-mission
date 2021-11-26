package com.github.nagyesta.abortmission.booster.cucumber.staticfire;

import com.github.nagyesta.abortmission.testkit.spring.Booster;
import com.github.nagyesta.abortmission.testkit.spring.StaticFire;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

@SuppressWarnings("NullableProblems")
@CucumberContextConfiguration
@ContextConfiguration(classes = StaticFire.class)
public final class StaticFireStepDefs implements ApplicationContextAware {

    private Booster centerCore;
    private Booster sideBooster;
    private ApplicationContext applicationContext;
    private boolean onFire;

    @Given("Center core is ready")
    public void centerCoreIsReady() {
        this.centerCore = applicationContext.getBean("centerCore", Booster.class);
    }

    @When("Static fire is ignited")
    public void staticFireIsIgnited() {
        boolean burning = true;
        if (centerCore != null) {
            burning = centerCore.isOnFire();
        }
        if (sideBooster != null) {
            burning &= sideBooster.isOnFire();
        }
        onFire = burning;
    }

    @Then("Fire is burning")
    public void fireIsBurning() {
        Assertions.assertTrue(onFire);
    }

    @Given("Side booster is ready ([0-9]+)")
    public void sideBoosterIsReady(final int ignore) {
        this.sideBooster = applicationContext.getBean("sideBooster", Booster.class);
    }

    @Given("Abort-Mission abort decisions are suppressed")
    public void abortMissionAbortDecisionsAreSuppressed() {
        throw new IllegalArgumentException();
    }

    @And("Center core and side boosters are ready")
    public void centerCoreAndSideBoostersAreReady() {
        this.centerCore = applicationContext.getBean("centerCore", Booster.class);
        this.sideBooster = applicationContext.getBean("sideBooster", Booster.class);
    }

    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
