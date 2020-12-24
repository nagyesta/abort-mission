package com.github.nagyesta.abortmission.booster.cucumber.parachute;

import com.github.nagyesta.abortmission.testkit.vanilla.ParachuteDrop;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;

public final class ParachuteStepDefs {

    private ParachuteDrop parachuteDrop;
    private boolean result;

    @Given("Capsule is dropped")
    public void capsuleIsDropped() {
        parachuteDrop = new ParachuteDrop();
    }

    @When("^Activates (-?[0-9]+) parachutes$")
    public void parachutesAreActivated(final int amount) {
        result = parachuteDrop.canOpenParachute(amount);
    }

    @Then("Parachutes are deployed")
    public void parachutesAreDeployed() {
        Assertions.assertTrue(result);
    }
}
