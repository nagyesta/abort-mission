package com.github.nagyesta.abortmission.reporting.html;

import lombok.Data;

import javax.annotation.Nonnull;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.github.nagyesta.abortmission.reporting.html.LaunchHtml.EMPTY;
import static com.github.nagyesta.abortmission.reporting.html.LaunchHtml.formatTimeMillis;

@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public final class StatsHtml {
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

    private StatsHtml(@Nonnull final StatsHtmlBuilder builder) {
        this.minStart = builder.minStart;
        this.maxEnd = builder.maxEnd;
        this.worstResult = builder.worstResult;
        this.count = builder.count;
        this.sumDuration = builder.sumDuration;
        this.minDuration = builder.minDuration;
        this.maxDuration = builder.maxDuration;
        this.avgDuration = builder.avgDuration;
        this.success = builder.success;
        this.failure = builder.failure;
        this.abort = builder.abort;
        this.suppressed = builder.suppressed;
    }

    public static StatsHtmlBuilder builder() {
        return new StatsHtmlBuilder();
    }

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

    @SuppressWarnings("checkstyle:HiddenField")
    public static class StatsHtmlBuilder {
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

        StatsHtmlBuilder() {
        }

        public StatsHtmlBuilder minStart(final LocalDateTime minStart) {
            this.minStart = minStart;
            return this;
        }

        public StatsHtmlBuilder maxEnd(final LocalDateTime maxEnd) {
            this.maxEnd = maxEnd;
            return this;
        }

        public StatsHtmlBuilder worstResult(final StageResultHtml worstResult) {
            this.worstResult = worstResult;
            return this;
        }

        public StatsHtmlBuilder count(final int count) {
            this.count = count;
            return this;
        }

        public StatsHtmlBuilder sumDuration(final int sumDuration) {
            this.sumDuration = sumDuration;
            return this;
        }

        public StatsHtmlBuilder minDuration(final int minDuration) {
            this.minDuration = minDuration;
            return this;
        }

        public StatsHtmlBuilder maxDuration(final int maxDuration) {
            this.maxDuration = maxDuration;
            return this;
        }

        public StatsHtmlBuilder avgDuration(final double avgDuration) {
            this.avgDuration = avgDuration;
            return this;
        }

        public StatsHtmlBuilder success(final int success) {
            this.success = success;
            return this;
        }

        public StatsHtmlBuilder failure(final int failure) {
            this.failure = failure;
            return this;
        }

        public StatsHtmlBuilder abort(final int abort) {
            this.abort = abort;
            return this;
        }

        public StatsHtmlBuilder suppressed(final int suppressed) {
            this.suppressed = suppressed;
            return this;
        }

        public StatsHtml build() {
            return new StatsHtml(this);
        }
    }
}
