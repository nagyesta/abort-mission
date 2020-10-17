package com.github.nagyesta.abortmission.testkit.vanilla;

public final class ParachuteDrop {

    private static final int NUMBER_OF_PARACHUTES = 3;

    public boolean canOpenParachute(final int index) {
        return index >= 0 && index < NUMBER_OF_PARACHUTES;
    }
}
