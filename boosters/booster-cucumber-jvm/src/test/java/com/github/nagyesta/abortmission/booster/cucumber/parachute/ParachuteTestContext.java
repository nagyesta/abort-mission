package com.github.nagyesta.abortmission.booster.cucumber.parachute;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = ParachuteTestContext.class)
@RunWith(Cucumber.class)
@CucumberOptions(glue = "com.github.nagyesta.abortmission.booster.cucumber.parachute")
public class ParachuteTestContext {
}
