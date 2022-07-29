package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.StageResultHtml;
import com.github.nagyesta.abortmission.reporting.html.StatsHtml;
import com.github.nagyesta.abortmission.reporting.json.StageResultJson;
import com.github.nagyesta.abortmission.reporting.json.StatsJson;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class StatsJsonToHtmlConverterTest {

    private static final String METHOD_NAME = "methodName";
    private static final String MATCHER_NAME = "matcherName";

    @SuppressWarnings("checkstyle:MagicNumber")
    private static Stream<Arguments> validInputProvider() {
        final StatsJson empty = new StatsJson();
        empty.setMinStart(LocalDateTime.now());
        empty.setMaxEnd(LocalDateTime.now());
        empty.setWorstResult(StageResultJson.SUCCESS);
        empty.setResultCount(null);
        final StatsJson full = new StatsJson();
        full.setMinStart(LocalDateTime.now());
        full.setMaxEnd(LocalDateTime.now());
        full.setWorstResult(StageResultJson.FAILURE);
        full.setCount(1);
        full.setSumDuration(2);
        full.setMinDuration(3);
        full.setAvgDuration(4.0D);
        full.setMaxDuration(5);
        final HashMap<StageResultJson, Integer> resultCount = new HashMap<>();
        resultCount.put(StageResultJson.SUCCESS, 6);
        resultCount.put(StageResultJson.FAILURE, 7);
        resultCount.put(StageResultJson.ABORT, 8);
        resultCount.put(StageResultJson.SUPPRESSED, 9);
        full.setResultCount(resultCount);
        return Stream.<Arguments>builder()
                .add(Arguments.of(empty, StatsHtml.builder()
                        .minStart(empty.getMinStart())
                        .maxEnd(empty.getMaxEnd())
                        .worstResult(StageResultHtml.SUCCESS)
                        .count(0)
                        .sumDuration(0)
                        .minDuration(0)
                        .avgDuration(0.0D)
                        .maxDuration(0)
                        .success(0)
                        .failure(0)
                        .abort(0)
                        .suppressed(0)
                        .build()))
                .add(Arguments.of(full, StatsHtml.builder()
                        .minStart(full.getMinStart())
                        .maxEnd(full.getMaxEnd())
                        .worstResult(StageResultHtml.FAILURE)
                        .count(1)
                        .sumDuration(2)
                        .minDuration(3)
                        .avgDuration(4.0D)
                        .maxDuration(5)
                        .success(6)
                        .failure(7)
                        .abort(8)
                        .suppressed(9)
                        .build()))
                .build();
    }

    @ParameterizedTest
    @MethodSource("validInputProvider")
    void testConvertShouldConvertNonNullValuesWhenCalled(final StatsJson input, final StatsHtml expected) {
        //given
        final StatsJsonToHtmlConverter underTest = new StatsJsonToHtmlConverter();

        //when
        final StatsHtml actual = underTest.apply(input);

        //then
        assertNotNull(actual);
        assertEquals(expected, actual);
    }
}
