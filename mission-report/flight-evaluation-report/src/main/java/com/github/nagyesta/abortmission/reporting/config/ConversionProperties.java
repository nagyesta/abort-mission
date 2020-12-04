package com.github.nagyesta.abortmission.reporting.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Data
@NoArgsConstructor
@Component
@ConfigurationProperties(prefix = "report", ignoreInvalidFields = false, ignoreUnknownFields = false)
public class ConversionProperties {
    @NonNull
    private File input;
    @NonNull
    private File output;
    private boolean relaxed = false;
}
