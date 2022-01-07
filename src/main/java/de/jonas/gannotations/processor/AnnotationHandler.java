package de.jonas.gannotations.processor;

import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import java.lang.annotation.Annotation;
import java.util.Set;

public interface AnnotationHandler {

    void processAnnotation(
        @NotNull final Set<? extends Element> annotatedElements,
        @NotNull final ProcessingEnvironment processingEnvironment
    );

    @NotNull
    Class<? extends Annotation> getAnnotytionType();

}
