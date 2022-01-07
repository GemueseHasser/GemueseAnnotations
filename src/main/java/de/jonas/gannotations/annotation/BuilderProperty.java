package de.jonas.gannotations.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mithilfe der {@link BuilderProperty} lässt sich sehr einfach ein Builder für ein bestimmtes Objekt erzeugen. Man kann
 * diese Annotation ausschließlich an Setter anmerken. Diese Setter werden dann genutzt, um den Builder zu erzeugen.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface BuilderProperty {
}
