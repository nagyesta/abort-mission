package com.github.nagyesta.abortmission.reporting.html;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.Map;
import java.util.SortedSet;

@Builder
@Data
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
