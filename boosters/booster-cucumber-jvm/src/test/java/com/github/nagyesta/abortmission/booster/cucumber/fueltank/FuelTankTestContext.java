package com.github.nagyesta.abortmission.booster.cucumber.fueltank;

import io.cucumber.junit.Cucumber;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;

@CucumberContextConfiguration
@ContextConfiguration(classes = FuelTankTestContext.class)
@RunWith(Cucumber.class)
public class FuelTankTestContext {
}
