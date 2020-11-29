package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.StageResultHtml;
import com.github.nagyesta.abortmission.reporting.html.StatsHtml;
import com.github.nagyesta.abortmission.reporting.json.LaunchJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component
public class LaunchJsonToHtmlConverter implements Converter<LaunchJson, LaunchHtml> {

    private final StatsJsonToHtmlConverter statsConverter;
    private final ClassJsonToHtmlConverter classConverter;

    @Autowired
    public LaunchJsonToHtmlConverter(final StatsJsonToHtmlConverter statsConverter,
                                     final ClassJsonToHtmlConverter classConverter) {
        this.statsConverter = statsConverter;
        this.classConverter = classConverter;
    }

    @Override
    public LaunchHtml convert(@NonNull final LaunchJson source) {
        Assert.notNull(source.getStats(), "Stats cannot be null.");
        final StatsHtml emptyStats = StatsHtml.builder()
                .worstResult(StageResultHtml.SUPPRESSED)
                .build();
        return LaunchHtml.builder()
                .stats(statsConverter.convert(source.getStats()))
                .classes(Optional.ofNullable(source.getClasses())
                        .map(Map::values)
                        .map(Collection::stream)
                        .map(s -> s.map(classConverter::convert).collect(Collectors.toCollection(TreeSet::new)))
                        .orElse(new TreeSet<>()))
                .countdownStats(Optional.ofNullable(source.getCountdownStats()).map(statsConverter::convert).orElse(emptyStats))
                .missionStats(Optional.ofNullable(source.getMissionStats()).map(statsConverter::convert).orElse(emptyStats))
                .build();
    }
}
