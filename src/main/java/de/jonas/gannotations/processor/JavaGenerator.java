package de.jonas.gannotations.processor;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.processing.ProcessingEnvironment;
import javax.tools.JavaFileObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.NavigableMap;

@NotNull
public final class JavaGenerator {

    @NotNull
    private final String newClassName;
    private PrintWriter writer;


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

        if (initializing == null) return;

        this.writer.print(" = ");
        this.writer.print(initializing + ";");
    }

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

    public void finish() {
        this.writer.println("}");
        this.writer.close();
    }

    @NotNull
    public String getNewClassName() {
        return this.newClassName;
    }

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

    private void printBody(@NotNull final String[] body) {
        for (@NotNull final String line : body) {
            this.writer.println("        " + line);
        }
    }

}
