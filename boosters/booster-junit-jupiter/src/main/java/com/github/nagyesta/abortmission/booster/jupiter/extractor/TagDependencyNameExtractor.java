package com.github.nagyesta.abortmission.booster.jupiter.extractor;

import com.github.nagyesta.abortmission.core.extractor.impl.AnnotationDependencyNameExtractor;
import org.junit.jupiter.api.Tag;

import java.util.Collection;
import java.util.Collections;

public class TagDependencyNameExtractor extends AnnotationDependencyNameExtractor<Tag> {

    @Override
    protected Class<Tag> getType() {
        return Tag.class;
    }

    @Override
    protected Collection<String> extractValuesFromAnnotation(final Tag annotation) {
        return Collections.singleton(annotation.value());
    }
}
