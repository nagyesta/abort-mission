package com.github.nagyesta.abortmission.reporting.config;

import lombok.Data;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.Optional;

import static com.github.nagyesta.abortmission.reporting.PropertiesParser.INPUT;
import static com.github.nagyesta.abortmission.reporting.PropertiesParser.OUTPUT;

@Data
public final class ConversionProperties {
    private File input;
    private File output;
    private boolean relaxed;
    private boolean failOnError;

    private ConversionProperties(
            final File input,
            final File output,
            final boolean relaxed,
            final boolean failOnError) {
        this.input = Optional.ofNullable(input)
                .orElseThrow(() -> new IllegalArgumentException("Missing input parameter. Expected: " + INPUT + "=<file.json>"));
        this.output = output;
        Optional.ofNullable(output)
                .orElseThrow(() -> new IllegalArgumentException("Missing output parameter. Expected: " + OUTPUT + "=<file.html>"));
        this.relaxed = relaxed;
        this.failOnError = failOnError;
    }

    public static ConversionPropertiesBuilder builder() {
        return new ConversionPropertiesBuilder();
    }

    @SuppressWarnings("checkstyle:HiddenField")
    public static final class ConversionPropertiesBuilder {
        private File input;
        private File output;
        private boolean relaxed = false;
        private boolean failOnError = false;

        ConversionPropertiesBuilder() {
        }

        public ConversionPropertiesBuilder input(@Nonnull final File input) {
            this.input = input;
            return this;
        }

        public ConversionPropertiesBuilder output(@Nonnull final File output) {
            this.output = output;
            return this;
        }

        public ConversionPropertiesBuilder relaxed(final boolean relaxed) {
            this.relaxed = relaxed;
            return this;
        }

        public ConversionPropertiesBuilder failOnError(final boolean failOnError) {
            this.failOnError = failOnError;
            return this;
        }

        public ConversionProperties build() {
            return new ConversionProperties(input, output, relaxed, failOnError);
        }
    }
}
