package com.github.nagyesta.abortmission.booster.cucumber.weather;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public final class WeatherStepDefs {

    private static final int THRESHOLD = 50;

    private int chanceOfStorm;
    private boolean success;

    @Given("We have {int} percentage chance of storms")
    public void checkWeather(final int chance) {
        this.chanceOfStorm = chance;
    }

    @Given("We don't check the weather")
    public void dontCheckWeather() {
        this.chanceOfStorm = 0;
    }

    @When("Launch is imminent")
    public void launchImminent() {
        success = chanceOfStorm < THRESHOLD;
    }

    @Then("Everything is fine")
    public void itIsFine() {
        Assertions.assertTrue(success, "Something is wrong.");
    }
}
