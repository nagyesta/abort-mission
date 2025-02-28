package com.github.nagyesta.abortmission.reporting;

import com.github.nagyesta.abortmission.reporting.config.ConversionProperties;

import java.io.File;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Pattern;

public final class PropertiesParser {

    /**
     * The parameter name of the input file.
     */
    public static final String INPUT = "--report.input";
    /**
     * The parameter name of the output file.
     */
    public static final String OUTPUT = "--report.output";
    private static final String RELAXED = "--report.relaxed";
    private static final String FAIL_ON_ERROR = "--report.failOnError";
    private static final String EQUALS = "=";
    private static final String EMPTY = "";

    private final List<String> args;

    public PropertiesParser(final List<String> args) {
        this.args = List.copyOf(args);
    }

    public ConversionProperties parseArguments() {
        final var builder = ConversionProperties.builder();
        parse(INPUT, File::new, builder::input);
        parse(OUTPUT, File::new, builder::output);
        parse(RELAXED, Boolean::valueOf, builder::relaxed);
        parse(FAIL_ON_ERROR, Boolean::valueOf, builder::failOnError);
        return builder.build();
    }

    private <T> void parse(final String parameter, final Function<String, T> mapper, final Consumer<T> consumer) {
        args.stream().filter(s -> s.startsWith(parameter)).findFirst()
                .map(s -> s.replaceFirst(Pattern.quote(parameter + EQUALS), EMPTY))
                .map(mapper).ifPresent(consumer);
    }
}
