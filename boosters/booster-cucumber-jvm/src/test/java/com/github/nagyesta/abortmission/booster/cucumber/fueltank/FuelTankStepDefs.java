package com.github.nagyesta.abortmission.booster.cucumber.fueltank;

import com.github.nagyesta.abortmission.testkit.vanilla.FuelTank;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public final class FuelTankStepDefs {

    private FuelTank fuelTank;
    private boolean success;

    @Given("Fuel tank exists")
    public void fuelTankExists() {
        fuelTank = new FuelTank();
    }

    @When("^(-?[0-9]+) units are loaded into the tank$")
    public void amountIsLoadedIntoTheTank(final int amount) {
        fuelTank.load(amount);
        success = true;
    }

    @Then("Fuel tank is intact")
    public void fuelTankIsIntact() {
        Assertions.assertTrue(success);
    }
}
