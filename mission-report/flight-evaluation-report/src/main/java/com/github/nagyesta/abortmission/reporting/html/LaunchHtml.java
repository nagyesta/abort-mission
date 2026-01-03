package com.github.nagyesta.abortmission.reporting.html;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;
import java.util.SortedSet;

@Builder
@Data
@JsonPropertyOrder({"matchers", "classNames", "methodNames", "runs"})
public final class LaunchHtml {

    @NonNull
    private final Map<String, String> matchers;
    @NonNull
    private final Map<String, String> classNames;
    @NonNull
    private final Map<String, String> methodNames;
    @NonNull
    private final SortedSet<TestRunHtml> runs;
}
