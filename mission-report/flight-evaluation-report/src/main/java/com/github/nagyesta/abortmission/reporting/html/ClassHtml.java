package com.github.nagyesta.abortmission.reporting.html;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.NonNull;

import java.util.Comparator;
import java.util.Map;

import static com.github.nagyesta.abortmission.reporting.html.LaunchHtml.shortHash;

@Builder
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public class ClassHtml implements Comparable<ClassHtml> {
    @lombok.NonNull
    private String classNameText;
    private StageLaunchStatsHtml countdown;
    private StatsHtml stats;
    private Map<String, StageLaunchStatsHtml> launches;

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

    @Override
    public int compareTo(@NonNull final ClassHtml o) {
        return Comparator.comparing(ClassHtml::getClassNameText).compare(this, o);
    }
}
