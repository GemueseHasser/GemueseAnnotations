package de.jonas.gannotations.processor;

import de.jonas.gannotations.processor.annotation.BuilderPropertyAnnotation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import java.util.HashSet;
import java.util.Set;

/**
 * Mithilfe dieses {@link GemueseProcessor Prozessors} werden alle Aktionen ausgeführt, die benötigt werden, um alle
 * durch die {@link SupportedAnnotationTypes Anmerkung} unterstützten Annotations zu implementieren. In diesem Prozessor
 * werden auch alle {@link AnnotationHandler} initialisiert und nach und nach aufgerufen, da die Annotations erst nach
 * Klassen sortiert werden. Dieser Prozessor stellt die Schnittstelle zu dem Java-Compiler dar. Er selber wird mithilfe
 * einer Datei, welche sich als Ressource unter META-INF/services befindet registriert.
 */
@NotNull
@SupportedAnnotationTypes({
    "de.jonas.gannotations.annotation.BuilderProperty",
    "de.jonas.gannotations.annotation.Getter",
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public final class GemueseProcessor extends AbstractProcessor {

    //<editor-fold desc="CONSTANTS">
    /** Alle {@link AnnotationHandler Handler}, welche die Aktionen der Annotations regeln. */
    @NotNull
    private static final AnnotationHandler[] ANNOTATION_HANDLER = new AnnotationHandler[]{
        new BuilderPropertyAnnotation(),
    };
    //</editor-fold>


    //<editor-fold desc="implementation">
    @Override
    public boolean process(
        final Set<? extends TypeElement> annotations,
        final RoundEnvironment roundEnvironment
    ) {
        // iterate over all supported annotations by this processor
        for (@NotNull final TypeElement annotation : annotations) {
            // get all with current annotation annotated elements
            final Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(annotation);
            final Set<Element> alreadyUsedAnnotatedElements = new HashSet<>();

            // iterate over all with current annotation annotated elements
            for (@NotNull final Element element : annotatedElements) {
                // separate the elements by their class
                final Set<Element> specifiedAnnotatedElements = new HashSet<>();

                // check if current element is already in use
                if (alreadyUsedAnnotatedElements.contains(element)) continue;

                // get the name of the class from current element
                final String elementClassName = ((TypeElement) element.getEnclosingElement())
                    .getQualifiedName()
                    .toString();

                // compare current element with all others elements
                for (@NotNull final Element innerElement : annotatedElements) {
                    // get the name of the class from the reference element
                    final String innerElementClassName = ((TypeElement) innerElement.getEnclosingElement())
                        .getQualifiedName()
                        .toString();

                    // check if the reference element is already in use
                    if (alreadyUsedAnnotatedElements.contains(innerElement)) continue;

                    // check if the reference element is in the same class
                    if (!elementClassName.equals(innerElementClassName)) continue;

                    // use element for current process
                    specifiedAnnotatedElements.add(innerElement);
                    alreadyUsedAnnotatedElements.add(innerElement);
                }

                // execute the process for the current class
                processForClass(annotation, specifiedAnnotatedElements);
            }
        }

        return true;
    }
    //</editor-fold>

    /**
     * Führt den Prozess für einen bestimmten Typen einer Annotation in nur einer Klasse aus.
     *
     * @param annotation        Die Annotation, für die dieser Prozess ausgeführt wird.
     * @param annotatedElements Alle Elemente in einer bestimmten Klasse, die diese Annotation erhalten haben.
     */
    private void processForClass(
        @NotNull final TypeElement annotation,
        @NotNull final Set<? extends Element> annotatedElements
    ) {
        // get matching handler
        for (@NotNull final AnnotationHandler annotationHandler : ANNOTATION_HANDLER) {
            if (!annotation.getQualifiedName().toString().equals(annotationHandler.getAnnotytionType().getName())) {
                continue;
            }

            // execute the process for the current annotations in one class
            annotationHandler.processAnnotation(annotatedElements, processingEnv);
        }
    }

}
