package de.jonas.gannotations.processor;

import com.sun.source.util.Trees;
import com.sun.tools.javac.tree.TreeMaker;
import org.jetbrains.annotations.NotNull;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Mithilfe eines {@link AnnotationHandler} lässt sich durch das Implementieren und anschließende Registrieren im {@link
 * GemueseProcessor} eine Annotation im Prozessor dieser Bibliothek registrieren und durch die vollständige
 * Implementierung auch die Aktion deklarieren, welche durch Anmerken dieser Annotation, welche in der Methode {@code
 * getAnnotytionType} zurückgegeben werden muss, während des Kompilierungs-Prozesses ausgeführt werden soll. Dieser
 * Handler bzw die Klasse, die diesen Handler implementiert, wird nur einmal bei der Registrierung instanziiert, jedoch
 * wird dieser Handler für jede Klasse, in der sich eine Annotation dieser Art befindet, separat ausgeführt.
 */
@NotNull
public interface AnnotationHandler {

    /**
     * Diese Methode wird für jede Klasse, welche mindestens eine Annotation dieses Typs beinhaltet ausgeführt. In
     * dieser Methode wird implementiert, was die Annotation für jede Klasse tun soll, die diese Annotation beinhaltet.
     *
     * @param annotatedElements     Alle Elemente in einer Klasse, die diese Annotation angemerkt haben.
     * @param processingEnvironment Die {@link ProcessingEnvironment}, die von dem Annotation-Prozessor übergeben wird
     *                              und die auch weiterhin bei der Implementierung genutzt werden kann.
     */
    void processAnnotation(
        @NotNull final Set<? extends Element> annotatedElements,
        @NotNull final ProcessingEnvironment processingEnvironment,
        @NotNull final Trees trees,
        @NotNull final TreeMaker treeMaker
    );

    /**
     * Der Typ der Annotation, also auf welche Annotation genau sich dieser Handler beziehen soll.
     *
     * @return Der Typ der Annotation, also auf welche Annotation genau sich dieser Handler beziehen soll.
     */
    @NotNull
    Class<? extends Annotation> getAnnotytionType();

}
