package com.github.nagyesta.abortmission.core.extractor;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Defines common behavior of the components we want to use to extract dependency names based on test methods/classes.
 */
public interface DependencyNameExtractor extends Function<Object, Optional<Set<String>>> {
}
