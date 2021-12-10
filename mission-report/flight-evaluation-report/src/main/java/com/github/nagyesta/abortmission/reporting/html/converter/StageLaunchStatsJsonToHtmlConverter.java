package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.LaunchHtml;
import com.github.nagyesta.abortmission.reporting.html.StageLaunchStatsHtml;
import com.github.nagyesta.abortmission.reporting.json.StageLaunchStatsJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class StageLaunchStatsJsonToHtmlConverter {

    private final StatsJsonToHtmlConverter statsConverter;

    @Autowired
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
    public StageLaunchStatsHtml convert(@NonNull final Function<String, String> titleConverter,
                                        @NonNull final String name,
                                        @NonNull final StageLaunchStatsJson source) {
        Assert.notNull(source.getStats(), "Stats cannot be null.");
        final Map<String, String> map = Optional.ofNullable(source.getMatcherNames())
                .map(Collection::stream)
                .map(s -> s.collect(Collectors.toMap(Function.identity(), LaunchHtml::shortHash)))
                .orElse(Collections.emptyMap());
        return StageLaunchStatsHtml.builder(name, statsConverter.convert(source.getStats()))
                .matcherNames(new TreeMap<>(map))
                .titleName(titleConverter.apply(name))
                .build();
    }
}
