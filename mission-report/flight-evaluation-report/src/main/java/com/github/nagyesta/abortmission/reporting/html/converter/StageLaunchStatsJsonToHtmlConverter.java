package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.StageLaunchStatsHtml;
import com.github.nagyesta.abortmission.reporting.json.StageLaunchStatsJson;
import com.github.nagyesta.abortmission.reporting.json.StatsJson;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StageLaunchStatsJsonToHtmlConverter {

    private final StatsJsonToHtmlConverter statsConverter;

    public StageLaunchStatsJsonToHtmlConverter(final StatsJsonToHtmlConverter statsConverter) {
        this.statsConverter = statsConverter;
    }

    /**
     * Converts the given source object into the HTML-friendly representation using the provided name (countdown/launch)
     * and the title converter function used for converting the provided name into a better formatted tooltip value.
     *
     * @param titleConverter The function that generates the tooltip value from the name.
     * @param name           The name of the method/countdown
     * @param source         The source object we want to convert.
     * @return The converted object
     */
    public StageLaunchStatsHtml apply(@Nonnull final Function<String, String> titleConverter,
                                      @Nonnull final String name,
                                      @Nonnull final StageLaunchStatsJson source) {
        final StatsJson stats = source.getStats();
        if (stats == null) {
            throw new IllegalArgumentException("Stats cannot be null for stage.");
        }
        final Map<String, String> map = Optional.ofNullable(source.getMatcherNames())
                .map(Collection::stream)
                .map(s -> s.collect(Collectors.toMap(Function.identity(), LaunchHtml::shortHash)))
                .orElse(Collections.emptyMap());
        return StageLaunchStatsHtml.builder(name, statsConverter.apply(stats))
                .matcherNames(new TreeMap<>(map))
                .titleName(titleConverter.apply(name))
                .build();
    }

}
