package com.github.nagyesta.abortmission.reporting.html;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.github.nagyesta.abortmission.reporting.html.LaunchHtml.EMPTY;
import static com.github.nagyesta.abortmission.reporting.html.LaunchHtml.formatTimeMillis;

@Builder
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public class StatsHtml {
    private LocalDateTime minStart;
    private LocalDateTime maxEnd;
    private StageResultHtml worstResult;
    private int count;
    private int sumDuration;
    private int minDuration;
    private int maxDuration;
    private double avgDuration;
    private int success;
    private int failure;
    private int abort;
    private int suppressed;

    public String getSumDurationAsText() {
        return formatTimeMillis(sumDuration);
    }

    public String cssIfSuccessInactive(final String className) {
        return Optional.of(className).filter(i -> success <= 0).orElse(EMPTY);
    }

    public String cssIfFailureInactive(final String className) {
        return Optional.of(className).filter(i -> failure <= 0).orElse(EMPTY);
    }

    public String cssIfAbortInactive(final String className) {
        return Optional.of(className).filter(i -> abort <= 0).orElse(EMPTY);
    }

    public String cssIfSuppressedInactive(final String className) {
        return Optional.of(className).filter(i -> suppressed <= 0).orElse(EMPTY);
    }
}
