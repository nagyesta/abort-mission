package com.github.nagyesta.abortmission.reporting.html;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.SortedMap;

import static com.github.nagyesta.abortmission.reporting.html.LaunchHtml.shortHash;

@Builder
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public class StageLaunchStatsHtml {
    @NonNull
    private String displayName;
    private String titleName;
    private SortedMap<String, String> matcherNames;
    @NonNull
    private StatsHtml stats;

    public String getId() {
        return shortHash(displayName);
    }

    public boolean isCollapsed() {
        return stats.getWorstResult() == StageResultHtml.SUCCESS || stats.getWorstResult() == StageResultHtml.SUPPRESSED;
    }
}
