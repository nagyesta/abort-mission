package com.github.nagyesta.abortmission.reporting.html.converter;

import com.github.nagyesta.abortmission.reporting.html.ClassHtml;
import com.github.nagyesta.abortmission.reporting.html.StageLaunchStatsHtml;
import com.github.nagyesta.abortmission.reporting.json.ClassJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ClassJsonToHtmlConverter implements Converter<ClassJson, ClassHtml> {

    private static final String TEST_METHOD_REGEX = "^test(?<what>[a-zA-Z0-9]+)Should(?<doWhat>[a-zA-Z0-9]+)When(?<when>[a-zA-Z0-9]+)$";

    private final StatsJsonToHtmlConverter statsConverter;
    private final StageLaunchStatsJsonToHtmlConverter stageConverter;

    @Autowired
    public ClassJsonToHtmlConverter(final StatsJsonToHtmlConverter statsConverter,
                                    final StageLaunchStatsJsonToHtmlConverter stageConverter) {
        this.statsConverter = statsConverter;
        this.stageConverter = stageConverter;
    }

    @Override
    public ClassHtml convert(@NonNull final ClassJson source) {
        final Map<String, StageLaunchStatsHtml> launchMap = convertLaunchMap(source);
        return ClassHtml.builder(source.getClassName())
                .countdown(Optional.ofNullable(source.getCountdown())
                        .map(c -> stageConverter.convert(Function.identity(), "Countdown", c))
                        .orElse(null))
                .stats(statsConverter.convert(source.getStats()))
                .launches(launchMap)
                .build();
    }

    /**
     * Converts a test method name to a tooltip text.
     *
     * @param s The name of the test method.
     * @return The tooltip
     */
    protected String convertTestMethod(@NonNull final String s) {
        final Pattern pattern = Pattern.compile(TEST_METHOD_REGEX);
        final Matcher matcher = pattern.matcher(s);
        if (!matcher.matches()) {
            return s;
        }
        return String.format("test\n%s\nShould\n%s\nWhen\n%s",
                matcher.group("what"), matcher.group("doWhat"), matcher.group("when"));
    }

    private Map<String, StageLaunchStatsHtml> convertLaunchMap(final ClassJson source) {
        return Optional.ofNullable(source.getLaunches())
                .map(Map::entrySet)
                .map(Collection::stream)
                .map(stream -> stream.collect(
                        Collectors.toMap(Map.Entry::getKey,
                                e -> stageConverter.convert(this::convertTestMethod, e.getKey(), e.getValue()))))
                .orElse(null);
    }
}
