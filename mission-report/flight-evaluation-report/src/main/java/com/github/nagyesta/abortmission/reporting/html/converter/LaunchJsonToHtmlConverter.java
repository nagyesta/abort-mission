package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.StageResultHtml;
import com.github.nagyesta.abortmission.reporting.html.StatsHtml;
import com.github.nagyesta.abortmission.reporting.json.LaunchJson;
import com.github.nagyesta.abortmission.reporting.json.StatsJson;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class LaunchJsonToHtmlConverter implements Function<LaunchJson, LaunchHtml> {

    private final StatsJsonToHtmlConverter statsConverter;
    private final ClassJsonToHtmlConverter classConverter;

    public LaunchJsonToHtmlConverter(final StatsJsonToHtmlConverter statsConverter,
                                     final ClassJsonToHtmlConverter classConverter) {
        this.statsConverter = statsConverter;
        this.classConverter = classConverter;
    }

    @Override
    public LaunchHtml apply(@Nonnull final LaunchJson source) {
        final StatsJson stats = source.getStats();
        if (stats == null) {
            throw new IllegalArgumentException("Stats cannot be null for launch.");
        }
        final StatsHtml emptyStats = StatsHtml.builder()
                .worstResult(StageResultHtml.SUPPRESSED)
                .build();
        return LaunchHtml.builder()
                .stats(statsConverter.apply(stats))
                .classes(Optional.ofNullable(source.getClasses())
                        .map(Map::values)
                        .map(Collection::stream)
                        .map(s -> s.map(classConverter).collect(Collectors.toCollection(TreeSet::new)))
                        .orElse(new TreeSet<>()))
                .countdownStats(Optional.ofNullable(source.getCountdownStats()).map(statsConverter).orElse(emptyStats))
                .missionStats(Optional.ofNullable(source.getMissionStats()).map(statsConverter).orElse(emptyStats))
                .build();
    }

}
