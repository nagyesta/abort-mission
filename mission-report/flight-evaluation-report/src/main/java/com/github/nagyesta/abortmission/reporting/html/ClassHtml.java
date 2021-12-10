package com.github.nagyesta.abortmission.reporting.html;

import lombok.Data;
import org.springframework.lang.NonNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static com.github.nagyesta.abortmission.reporting.html.LaunchHtml.shortHash;

@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public final class ClassHtml implements Comparable<ClassHtml> {
    @lombok.NonNull
    private String classNameText;
    private StageLaunchStatsHtml countdown;
    private StatsHtml stats;
    private Map<String, StageLaunchStatsHtml> launches;

    private ClassHtml(@NonNull final ClassHtmlBuilder builder) {
        this.classNameText = builder.classNameText;
        this.countdown = builder.countdown;
        this.stats = builder.stats;
        this.launches = builder.launches;
    }

    public static ClassHtmlBuilder builder(@lombok.NonNull final String classNameText) {
        return new ClassHtmlBuilder(classNameText);
    }

    public String getId() {
        return shortHash(classNameText);
    }

    public String getClassNameTextShort() {
        return classNameText
                .replaceFirst("^classpath:", "")
                .replaceFirst("\\.feature$", "")
                .replaceAll("([a-z]{2})[a-z]+([./])", "$1$2");
    }

    public String getClassNameTitle() {
        return classNameText;
    }

    public boolean isCollapsed() {
        return stats.getWorstResult() == StageResultHtml.SUCCESS;
    }

    public long startTimeEpochMillis() {
        return Optional.ofNullable(stats)
                .map(StatsHtml::getMinStart)
                .map((LocalDateTime offset) -> offset.toInstant(ZoneOffset.UTC))
                .map(Instant::toEpochMilli)
                .orElse(0L);
    }

    @Override
    public int compareTo(@NonNull final ClassHtml o) {
        return Comparator.comparing(ClassHtml::startTimeEpochMillis)
                .thenComparing(ClassHtml::getClassNameText)
                .compare(this, o);
    }

    @SuppressWarnings("checkstyle:HiddenField")
    public static class ClassHtmlBuilder {
        private final String classNameText;
        private StageLaunchStatsHtml countdown;
        private StatsHtml stats;
        private Map<String, StageLaunchStatsHtml> launches;

        ClassHtmlBuilder(final String classNameText) {
            this.classNameText = classNameText;
        }

        public ClassHtmlBuilder countdown(final StageLaunchStatsHtml countdown) {
            this.countdown = countdown;
            return this;
        }

        public ClassHtmlBuilder stats(final StatsHtml stats) {
            this.stats = stats;
            return this;
        }

        public ClassHtmlBuilder launches(final Map<String, StageLaunchStatsHtml> launches) {
            this.launches = Optional.ofNullable(launches).map(TreeMap::new).orElse(null);
            return this;
        }

        public ClassHtml build() {
            return new ClassHtml(this);
        }

        public String toString() {
            return "ClassHtml.ClassHtmlBuilder(classNameText=" + this.classNameText
                    + ", countdown=" + this.countdown
                    + ", stats=" + this.stats
                    + ", launches=" + this.launches + ")";
        }
    }
}
