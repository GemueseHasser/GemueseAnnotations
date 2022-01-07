package de.jonas.gannotations.processor.annotation;

import de.jonas.gannotations.annotation.Getter;
import de.jonas.gannotations.processor.AnnotationHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

import java.lang.annotation.Annotation;
import java.util.Set;

public class GetterAnnotation implements AnnotationHandler {
    @Override
    public void processAnnotation(
        final @NotNull Set<? extends Element> annotatedElements,
        final @NotNull ProcessingEnvironment processingEnvironment
    ) {
        processingEnvironment.getMessager().printMessage(Diagnostic.Kind.NOTE, "Test-Nachricht #Getter");
    }

    @Override
    public @NotNull Class<? extends Annotation> getAnnotytionType() {
        return Getter.class;
    }
}
