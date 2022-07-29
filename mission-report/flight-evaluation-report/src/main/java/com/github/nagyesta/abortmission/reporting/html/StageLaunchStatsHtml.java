package com.github.nagyesta.abortmission.reporting.html;

import lombok.Data;
import lombok.NonNull;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import static com.github.nagyesta.abortmission.reporting.html.LaunchHtml.shortHash;

@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public final class StageLaunchStatsHtml {
    @NonNull
    private String displayName;
    private String titleName;
    private SortedMap<String, String> matcherNames;
    @NonNull
    private StatsHtml stats;

    private StageLaunchStatsHtml(@Nonnull final StageLaunchStatsHtmlBuilder builder) {
        this.displayName = builder.displayName;
        this.titleName = builder.titleName;
        this.matcherNames = builder.matcherNames;
        this.stats = builder.stats;
    }

    public static StageLaunchStatsHtmlBuilder builder(final String displayName, final StatsHtml stats) {
        return new StageLaunchStatsHtmlBuilder(displayName, stats);
    }

    public String getId() {
        return shortHash(displayName);
    }

    public boolean isCollapsed() {
        return stats.getWorstResult() == StageResultHtml.SUCCESS || stats.getWorstResult() == StageResultHtml.SUPPRESSED;
    }

    @SuppressWarnings("checkstyle:HiddenField")
    public static class StageLaunchStatsHtmlBuilder {
        private final String displayName;
        private final StatsHtml stats;
        private String titleName;
        private SortedMap<String, String> matcherNames;

        StageLaunchStatsHtmlBuilder(@NonNull final String displayName, @NonNull final StatsHtml stats) {
            this.displayName = displayName;
            this.stats = stats;
        }

        public StageLaunchStatsHtmlBuilder titleName(final String titleName) {
            this.titleName = titleName;
            return this;
        }

        public StageLaunchStatsHtmlBuilder matcherNames(final SortedMap<String, String> matcherNames) {
            this.matcherNames = Optional.ofNullable(matcherNames).map(TreeMap::new).orElse(null);
            return this;
        }

        public StageLaunchStatsHtml build() {
            return new StageLaunchStatsHtml(this);
        }
    }
}
