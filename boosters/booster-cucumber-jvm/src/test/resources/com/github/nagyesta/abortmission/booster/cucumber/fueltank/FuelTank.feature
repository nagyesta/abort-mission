Feature: Fuel tank tests

  @AbortMission_Context_fuelTank
    @AbortMission_SuppressFailure_com.example.SomeSillyException
    @AbortMission_SuppressFailure_java.lang.UnsupportedOperationException
    @AbortMission_SuppressFailure_com.github.nagyesta.abortmission.booster.cucumber.CucumberLaunchSequenceTemplate
  Scenario Outline: Fuel_1 Fuel tank should fill
    Given Fuel tank exists
    When <amount> units are loaded into the tank
    Then Fuel tank is intact

    Examples:
      | amount |
      | -1     |
      | 42     |
      | 5001   |
      | 420000 |
      | 500000 |
