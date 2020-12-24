Feature: Parachute drop tests

  Scenario Outline: Parachute_1 Parachutes should open
    Given Capsule is dropped
    When Activates <amount> parachutes
    Then Parachutes are deployed

    Examples:
      | amount |
      | 0      |
      | 1      |
      | 2      |
      | 3      |
      | 4      |
      | 5      |
      | 6      |
      | 7      |
      | 8      |
      | 9      |
