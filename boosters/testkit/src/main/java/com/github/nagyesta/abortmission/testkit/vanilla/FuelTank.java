package com.github.nagyesta.abortmission.testkit.vanilla;

public final class FuelTank {

    private static final int MAX_CAPACITY = 5000;
    private int propellantOnBoard = 0;

    public void load(final int amountKilograms) {
        if (amountKilograms < 0) {
            throw new UnsupportedOperationException("Cannot load negative amount of propellant.");
        }
        propellantOnBoard += amountKilograms;
        if (propellantOnBoard > MAX_CAPACITY) {
            throw new IllegalStateException("Fuel tank exploded.");
        }
    }
}
