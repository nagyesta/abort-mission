package com.github.nagyesta.abortmission.booster.testng.extractor;

import com.github.nagyesta.abortmission.core.extractor.impl.AnnotationDependencyNameExtractor;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;

public class GroupDependencyNameExtractor extends AnnotationDependencyNameExtractor<Test> {

    @Override
    protected Class<Test> getType() {
        return Test.class;
    }

    @Override
    protected Collection<String> extractValuesFromAnnotation(final Test annotation) {
        return Arrays.asList(annotation.groups());
    }
}
