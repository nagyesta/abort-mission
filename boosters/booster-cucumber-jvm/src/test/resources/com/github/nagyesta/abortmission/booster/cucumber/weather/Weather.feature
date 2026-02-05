Feature: Fuel tank tests

  @AbortMission_Context_weather
  Scenario Outline: Weather_1 We should launch when there is no storm
    Given We have <percent> percentage chance of storms
    When Launch is imminent
    Then Everything is fine

    Examples:
      | percent |
      | 1       |
      | 42      |
      | 70      |
      | 100     |

  @AbortMission_Context_weather
  Scenario: Launch_1 We ignore the weather
    Given We don't check the weather
    When Launch is imminent
    Then Everything is fine

  @AbortMission_Context_weather
  Scenario: Launch_2 Weather who?
    When Launch is imminent
    Then Everything is fine
