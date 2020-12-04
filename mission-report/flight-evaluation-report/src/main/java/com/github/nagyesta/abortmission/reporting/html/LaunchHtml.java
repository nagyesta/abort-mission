package com.github.nagyesta.abortmission.reporting.html;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.SortedSet;

@Builder
@Data
public class LaunchHtml {
    /**
     * Empty string for HTML default value calculation.
     */
    public static final String EMPTY = "";
    private static final String UNIT_HOUR = "h ";
    private static final String UNIT_MINUTE = "m ";
    private static final String UNIT_SECOND = "s";
    private static final String UNIT_MILLISECOND = "ms";
    private static final String SPACE = " ";
    private static final int HASH_RADIX = 32;
    private static final int MILLIS_IN_A_SECOND = 1000;

    private SortedSet<ClassHtml> classes;
    private StatsHtml stats;
    private StatsHtml countdownStats;
    private StatsHtml missionStats;

    /**
     * Hashes and shortens a given string (meant to be used for class/method Id generation).
     *
     * @param displayName The display name of the item we need to hash.
     * @return The hashed and shortened value.
     */
    public static String shortHash(final String displayName) {
        return Integer.toString(Math.abs(displayName.hashCode()), HASH_RADIX);
    }

    /**
     * Formats the given millisecond value in a human readable way (hours, minutes, seconds).
     *
     * @param millis The duration in milliseconds.
     * @return The human-readable format.
     */
    public static String formatTimeMillis(final long millis) {
        Duration timeLeft = Duration.of(millis, ChronoUnit.MILLIS);
        final long hours = timeLeft.toHours();
        timeLeft = timeLeft.minusHours(hours);
        final long minutes = timeLeft.toMinutes();
        timeLeft = timeLeft.minusMinutes(minutes);
        final long seconds = timeLeft.getSeconds();

        final StringBuilder builder = new StringBuilder();
        if (hours > 0) {
            builder.append(hours).append(UNIT_HOUR);
        }
        if (minutes > 0 || hours > 0) {
            builder.append(minutes).append(UNIT_MINUTE);
        }
        builder.append(seconds).append(UNIT_SECOND);
        if (hours == 0 && minutes == 0) {
            builder.append(SPACE).append(millis % MILLIS_IN_A_SECOND).append(UNIT_MILLISECOND);
        }
        return builder.toString();
    }
}
