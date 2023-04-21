package com.github.nagyesta.abortmission.reporting.json;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public class TestRunJson implements Comparable<TestRunJson> {

    private UUID launchId;
    private StageResultJson result;
    private long start;
    private long end;
    private String displayName;
    private String threadName;
    private String throwableClass;
    private String throwableMessage;
    private List<String> stackTrace;

    @SuppressWarnings("NullableProblems")
    @Override
    public int compareTo(final TestRunJson o) {
        return Comparator.comparing(TestRunJson::getStart)
                .thenComparing(TestRunJson::getEnd)
                .thenComparing(TestRunJson::getResult)
                .thenComparing(TestRunJson::getLaunchId)
                .compare(this, o);
    }

    public boolean isFailure() {
        return result == StageResultJson.FAILURE;
    }
}
