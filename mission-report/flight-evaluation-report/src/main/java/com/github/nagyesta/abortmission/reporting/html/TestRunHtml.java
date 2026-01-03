package com.github.nagyesta.abortmission.reporting.html;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.nagyesta.abortmission.reporting.json.StageResultJson;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
@JsonPropertyOrder({
        "classKey", "methodKey", "countdown", "matcherKeys", "launchId", "result",
        "start", "end", "displayName", "threadName", "throwableClass", "throwableMessage", "stackTrace"})
public final class TestRunHtml implements Comparable<TestRunHtml> {

    @NonNull
    private final String classKey;
    private final String methodKey;
    private final boolean countdown;
    @NonNull
    private final Set<String> matcherKeys;
    @NonNull
    private final UUID launchId;
    @NonNull
    private final StageResultJson result;
    private final long start;
    private final long end;
    @NonNull
    private final String displayName;
    @NonNull
    private final String threadName;
    private final String throwableClass;
    private final String throwableMessage;
    private final List<String> stackTrace;

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(final TestRunHtml o) {
        return Comparator.comparing(TestRunHtml::getStart)
                .thenComparing(TestRunHtml::getEnd)
                .thenComparing(TestRunHtml::getResult)
                .thenComparing(TestRunHtml::getLaunchId)
                .compare(this, o);
    }
}

