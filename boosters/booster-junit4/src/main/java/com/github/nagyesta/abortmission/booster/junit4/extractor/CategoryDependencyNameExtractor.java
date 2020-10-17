package com.github.nagyesta.abortmission.booster.junit4.extractor;

import com.github.nagyesta.abortmission.core.extractor.impl.AnnotationDependencyNameExtractor;
import org.junit.experimental.categories.Category;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CategoryDependencyNameExtractor extends AnnotationDependencyNameExtractor<Category> {

    @Override
    protected Class<Category> getType() {
        return Category.class;
    }

    @Override
    protected Collection<String> extractValuesFromAnnotation(final Category annotation) {
        return Arrays.stream(annotation.value())
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());
    }
}
