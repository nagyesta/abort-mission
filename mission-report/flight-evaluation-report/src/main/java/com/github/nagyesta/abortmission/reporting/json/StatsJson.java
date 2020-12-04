package com.github.nagyesta.abortmission.reporting.json;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.TreeMap;

@NoArgsConstructor
@Data
@SuppressWarnings({"checkstyle:DesignForExtension", "checkstyle:JavadocVariable"})
public class StatsJson {
    private LocalDateTime minStart;
    private LocalDateTime maxEnd;
    private StageResultJson worstResult;
    private int count;
    private int sumDuration;
    private Integer minDuration;
    private Integer maxDuration;
    private Double avgDuration;
    private Map<StageResultJson, Integer> resultCount = new TreeMap<>();

    @JsonSetter("minStart")
    public void setMinStartAsJson(final LocalDateTimeJson minStartAsJson) {
        this.minStart = convertLocalDateTime(minStartAsJson);
    }

    @JsonSetter("maxEnd")
    public void setMaxEndAsJson(final LocalDateTimeJson maxEndAsJson) {
        this.maxEnd = convertLocalDateTime(maxEndAsJson);
    }

    private LocalDateTime convertLocalDateTime(final LocalDateTimeJson input) {
        return LocalDateTime.of(input.getDate().getYear(),
                input.getDate().getMonth(),
                input.getDate().getDay(),
                input.getTime().getHour(),
                input.getTime().getMinute(),
                input.getTime().getSecond(),
                input.getTime().getNano());
    }
}
