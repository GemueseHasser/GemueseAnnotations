package de.jonas.gannotations.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.NavigableMap;

/**
 * Mithilfe des {@link JavaGenerator} lässt sich eine Java-Datei einfach und sauber erzeugen. Man kann ausschließlich
 * eine neue Datei erzeugen und keine bestehende Datei bearbeiten.
 */
@NotNull
public final class JavaGenerator {

    //<editor-fold desc="LOCAL FIELDS">
    /** Der Name der Klasse, die neu erstellt werden soll. */
    @NotNull
    private final String newClassName;
    /** Der {@link PrintWriter}, mit dem die gesamte Java-Datei geschrieben wird. */
    private PrintWriter writer;
    //</editor-fold>


    //<editor-fold desc="CONSTRUCTORS">

    /**
     * Erzeugt eine neue und vollständig unabhängige Instanz eines {@link JavaGenerator}. Mithilfe des {@link
     * JavaGenerator} lässt sich eine Java-Datei einfach und sauber erzeugen. Man kann ausschließlich eine neue Datei
     * erzeugen und keine bestehende Datei bearbeiten. Beim Erzeugen des Konstruktors wird der grundlegende Aufbau der
     * Java-Datei schonmal niedergeschrieben. Es wird nur die Schlussklammer ('}') noch nicht gesetzt. Diese wird durch
     * die Methode {@code finish} gesetzt.
     *
     * @param className     Der Name der aktuellen Klasse.
     * @param newClassName  Der Name, den die neue Klasse erhalten soll.
     * @param processingEnv Die {@link ProcessingEnvironment}, die vom Prozessor übergeben wird.
     */
    public JavaGenerator(
        @NotNull final String className,
        @NotNull final String newClassName,
        @NotNull final ProcessingEnvironment processingEnv
    ) {
        this.newClassName = newClassName;

        String packageName = null;
        final int lastDot = className.lastIndexOf('.');

        final String simpleNewClassName = newClassName.substring(lastDot + 1);

        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        try {
            final JavaFileObject file = processingEnv.getFiler().createSourceFile(newClassName);
            this.writer = new PrintWriter(file.openWriter());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if (packageName != null) {
            this.writer.print("package ");
            this.writer.print(packageName);
            this.writer.print(";");
            this.writer.println();
        }

        this.writer.println();
        this.writer.print("public final class ");
        this.writer.print(simpleNewClassName);
        this.writer.print(" {");
        this.writer.println();
    }
    //</editor-fold>


    /**
     * Fügt der zu generierenden Klasse ein Feld hinzu.
     *
     * @param modifier     Der Zugriffsmodifikator des Feldes (Bsp: 'private final').
     * @param type         Der Typ, den das Feld haben soll (Bsp: String).
     * @param name         Der Name des Feldes.
     * @param initializing Die Initialisierung des Feldes (null, wenn das Feld nicht initialisiert werden soll).
     */
    public void addField(
        @NotNull final String modifier,
        @NotNull final String type,
        @NotNull final String name,
        @Nullable final String initializing
    ) {
        this.writer.println();
        this.writer.print("    " + modifier);
        this.writer.print(" " + type);
        this.writer.print(" " + name);

        if (initializing != null) {
            this.writer.print(" = ");
            this.writer.print(initializing);
        }

        this.writer.print(";");
    }

    /**
     * Fügt der zu generierenden Klasse einen Konstruktor hinzu.
     *
     * @param parameters Die Parameter, die der Konstruktor bekommen soll.
     * @param body       Der Inhalt, der im Konstruktor stehen soll (ein Eintrag stellt eine Zeile dar).
     */
    public void addConstructor(
        @NotNull final NavigableMap<String, String> parameters,
        @NotNull final String[] body
    ) {
        this.writer.println();
        this.writer.println();

        this.writer.print("    public ");
        this.writer.print(this.newClassName);

        writeParameters(parameters);

        this.writer.print(" {");
        this.writer.println();

        printBody(body);

        this.writer.println("    }");
    }

    /**
     * Fügt der zu generierenden Klasse eine Methode hinzu.
     *
     * @param name       Der Name der Methode.
     * @param returnType Der Return-Type der Methode.
     * @param parameters Die Parameter, die die Methode bekommen soll.
     * @param body       Der Inhalt, der in dieser Methode stehen soll (ein Eintrag stellt eine Zeile dar).
     */
    public void addMethod(
        @NotNull final String name,
        @Nullable final String returnType,
        @NotNull final NavigableMap<String, String> parameters,
        @NotNull final String[] body
    ) {
        final String finalReturnType = (returnType == null) ? "void" : returnType;

        this.writer.println();
        this.writer.println();

        this.writer.print("    public ");
        this.writer.print(finalReturnType);
        this.writer.print(" ");
        this.writer.print(name);

        writeParameters(parameters);

        this.writer.print(" {");
        this.writer.println();

        printBody(body);

        this.writer.println("    }");
    }

    /**
     * Beendet die Bearbeitung dieses Generators und setzt die Schlussklammer ('}') der Klasse. Nachdem diese Methode
     * aufgerufen wurde, kann keine weitere Änderung mithilfe dieses Generators mehr vorgenommen werden, da der {@link
     * PrintWriter} geschlossen wird.
     */
    public void finish() {
        this.writer.println("}");
        this.writer.close();
    }

    /**
     * Gibt den Namen der Klasse zurück, die neu erzeugt werden soll.
     *
     * @return Der Name der Klasse, die neu erzeugtb werden soll.
     */
    @NotNull
    public String getNewClassName() {
        return this.newClassName;
    }

    /**
     * Fügt an der Stelle, an der sich der {@link PrintWriter} momentan befindet Parameter, welche durch Klammern
     * eingeschlossen sind hinzu.
     *
     * @param parameters Die Parameter, die niedergeschrieben werden sollen.
     */
    private void writeParameters(@NotNull NavigableMap<String, String> parameters) {
        this.writer.print("(");

        for (@NotNull final Map.Entry<String, String> entry : parameters.entrySet()) {
            final String paramObject = entry.getKey();
            final String paramName = entry.getValue();

            this.writer.print("final ");
            this.writer.print(paramObject);
            this.writer.print(" ");
            this.writer.print(paramName);

            if (entry.equals(parameters.lastEntry())) continue;

            this.writer.print(", ");
        }

        this.writer.print(")");
    }

    /**
     * Erzeugt an der Stelle, an der sich der {@link PrintWriter} momentan befindet Code, welcher in einem Array
     * abgespeichert ist, wo ein Eintrag eine Zeile darstellt.
     *
     * @param body Der Inhalt, welcher niedergeschrieben werden soll.
     */
    private void printBody(@NotNull final String[] body) {
        for (@NotNull final String line : body) {
            this.writer.println("        " + line);
        }
    }

}
