package de.jonas.gannotations.processor;

import de.jonas.gannotations.processor.annotation.BuilderPropertyAnnotation;
import de.jonas.gannotations.processor.annotation.GetterAnnotation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

import java.util.HashSet;
import java.util.Set;

@SupportedAnnotationTypes({
    "de.jonas.gannotations.annotation.BuilderProperty",
    "de.jonas.gannotations.annotation.Getter",
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public final class GemueseProcessor extends AbstractProcessor {

    private static final AnnotationHandler[] ANNOTATION_HANDLER = new AnnotationHandler[]{
        new BuilderPropertyAnnotation(),
        new GetterAnnotation(),
    };

    @Override
    public boolean process(
        final Set<? extends TypeElement> annotations,
        final RoundEnvironment roundEnvironment
    ) {
        for (@NotNull final TypeElement annotation : annotations) {
            final Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(annotation);
            final Set<Element> alreadyUsedAnnotatedElements = new HashSet<>();

            for (@NotNull final Element element : annotatedElements) {
                final Set<Element> specifiedAnnotatedElements = new HashSet<>();

                if (alreadyUsedAnnotatedElements.contains(element)) continue;

                final Name elementName = processingEnv.getElementUtils().getBinaryName((TypeElement) element);

                for (@NotNull final Element innerElement : annotatedElements) {
                    final Name innerElementName = processingEnv.getElementUtils().getBinaryName((TypeElement) innerElement);

                    if (!elementName.equals(innerElementName)) continue;

                    if (alreadyUsedAnnotatedElements.contains(innerElement)) continue;

                    specifiedAnnotatedElements.add(innerElement);
                    alreadyUsedAnnotatedElements.add(innerElement);
                }

                processForClass(annotation, specifiedAnnotatedElements);
                processingEnv.getMessager().printMessage(
                    Diagnostic.Kind.NOTE,
                    "Klasse: " + processingEnv.getElementUtils().getBinaryName((TypeElement) element) + " | Ann: " + specifiedAnnotatedElements
                );
            }
        }

        return true;
    }

    private void processForClass(
        @NotNull final TypeElement annotation,
        @NotNull final Set<? extends Element> annotatedElements
    ) {
        for (@NotNull final AnnotationHandler annotationHandler : ANNOTATION_HANDLER) {
            if (!annotation.getQualifiedName().toString().equals(annotationHandler.getAnnotytionType().getName())) {
                continue;
            }

            annotationHandler.processAnnotation(annotatedElements, processingEnv);
        }
    }

}
