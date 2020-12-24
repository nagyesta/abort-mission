@AbortMission_Context_StaticFire
@AbortMission_SuppressAbort
@AbortMission_SuppressFailure_java.lang.Exception
Feature: Static fire tests for center core

  Background:
    Given Center core is ready

  @Booster @CenterCore
  Scenario: StaticFire_1 Center core static fire is burning
    When Static fire is ignited
    Then Fire is burning

  Scenario: StaticFire_2 Center core static fire is burning
    When Static fire is ignited
    Then Fire is burning
