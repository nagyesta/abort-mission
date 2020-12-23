package com.github.nagyesta.abortmission.booster.cucumber.staticfire;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(glue = "com.github.nagyesta.abortmission.booster.cucumber.staticfire",
        plugin = "com.github.nagyesta.abortmission.booster.cucumber.AbortMissionPlugin")
public class StaticFireTestContext {
}
