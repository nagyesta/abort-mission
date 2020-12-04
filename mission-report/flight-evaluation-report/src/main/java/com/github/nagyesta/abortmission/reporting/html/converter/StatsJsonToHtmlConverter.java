package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.StageResultHtml;
import com.github.nagyesta.abortmission.reporting.html.StatsHtml;
import com.github.nagyesta.abortmission.reporting.json.StageResultJson;
import com.github.nagyesta.abortmission.reporting.json.StatsJson;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StatsJsonToHtmlConverter implements Converter<StatsJson, StatsHtml> {

    @Override
    public StatsHtml convert(@NonNull final StatsJson source) {
        return StatsHtml.builder()
                .minStart(source.getMinStart())
                .maxEnd(source.getMaxEnd())
                .worstResult(StageResultHtml.valueOf(source.getWorstResult().name()))
                .count(source.getCount())
                .sumDuration(source.getSumDuration())
                .minDuration(Optional.ofNullable(source.getMinDuration()).orElse(0))
                .avgDuration(Optional.ofNullable(source.getAvgDuration()).orElse(0.0D))
                .maxDuration(Optional.ofNullable(source.getMaxDuration()).orElse(0))
                .success(getOrDefault(source, StageResultJson.SUCCESS))
                .failure(getOrDefault(source, StageResultJson.FAILURE))
                .abort(getOrDefault(source, StageResultJson.ABORT))
                .suppressed(getOrDefault(source, StageResultJson.SUPPRESSED))
                .build();
    }

    private Integer getOrDefault(final StatsJson source, final StageResultJson status) {
        return Optional.ofNullable(source.getResultCount())
                .map(m -> m.get(status))
                .orElse(0);
    }
}
