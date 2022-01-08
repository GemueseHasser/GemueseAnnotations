package de.jonas.gannotations.processor.annotation;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.TreeMaker;
import de.jonas.gannotations.annotation.BuilderProperty;
import de.jonas.gannotations.processor.AnnotationHandler;
import de.jonas.gannotations.processor.JavaGenerator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ExecutableType;
import javax.tools.Diagnostic;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Es wird ein Handler für die {@link BuilderProperty} erzeugt, welcher nun aus allen Settern, die diese Annotation
 * besitzen einen Builder erzeugt.
 */
@NotNull
public final class BuilderPropertyAnnotation implements AnnotationHandler {

    //<editor-fold desc="implementation">
    @Override
    public void processAnnotation(
        @NotNull final Set<? extends Element> annotatedElements,
        @NotNull final ProcessingEnvironment processingEnvironment,
        @NotNull final Trees trees,
        @NotNull final TreeMaker treeMaker
    ) {
        final Map<Boolean, List<Element>> annotatedMethods = annotatedElements.stream().collect(
            Collectors.partitioningBy(element -> ((ExecutableType) element.asType()).getParameterTypes().size() == 1
                && element.getSimpleName().toString().startsWith("set")
            )
        );

        final List<Element> setters = annotatedMethods.get(true);
        final List<Element> otherMethods = annotatedMethods.get(false);

        otherMethods.forEach(element -> processingEnvironment.getMessager().printMessage(
                Diagnostic.Kind.ERROR,
                "@BuilderProperty must be applied to a setXxx method with a single argument",
                element
            )
        );

        if (setters.isEmpty()) {
            return;
        }

        final String className = ((TypeElement) setters.get(0).getEnclosingElement()).getQualifiedName().toString();

        final Map<String, String> setterMap = setters.stream().collect(Collectors.toMap(
                setter -> setter.getSimpleName().toString(),
                setter -> ((ExecutableType) setter.asType()).getParameterTypes().get(0).toString()
            )
        );

        generateJavaCode(className, processingEnvironment, setterMap);
    }

    @NotNull
    @Override
    public Class<? extends Annotation> getAnnotytionType() {
        return BuilderProperty.class;
    }
    //</editor-fold>

    /**
     * Generiert die Java-Datei des Builders.
     *
     * @param className             Der Name der Klasse, in der sich die Annotations befinden.
     * @param processingEnvironment Die {@link ProcessingEnvironment}, welche vom Prozessor übergeben wird.
     * @param setterMap             Alle Setter, welche für den Builder genutzt werden sollen.
     */
    public void generateJavaCode(
        @NotNull final String className,
        @NotNull final ProcessingEnvironment processingEnvironment,
        @NotNull final Map<String, String> setterMap
    ) {
        final JavaGenerator generator = new JavaGenerator(
            className,
            className + "Builder",
            processingEnvironment
        );

        generator.addField(
            "private final",
            className,
            "object",
            "new " + className + "()"
        );

        generator.addMethod(
            "build",
            className,
            Collections.emptyNavigableMap(),
            new String[]{
                "return object;"
            }
        );

        setterMap.forEach((methodName, argumentType) -> {
            final NavigableMap<String, String> parameters = new TreeMap<>();
            parameters.put(argumentType, "value");

            generator.addMethod(
                methodName,
                generator.getNewClassName(),
                parameters,
                new String[]{
                    "object." + methodName + "(value);",
                    "return this;"
                }
            );
        });

        generator.finish();
    }
}
