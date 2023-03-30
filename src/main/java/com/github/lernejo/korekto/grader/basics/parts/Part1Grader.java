package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.OS;
import com.github.lernejo.korekto.toolkit.misc.Processes;

import javax.tools.*;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.lernejo.korekto.grader.basics.parts.LaunchingContext.easyEquals;

public class Part1Grader implements PartGrader<LaunchingContext> {
    @Override
    public String name() {
        System.out.println();
        return "Hello World";
    }

    @Override
    public Double maxGrade() {
        return 2D;
    }

    @Override
    public GradePart grade(LaunchingContext context) {
        Path binPath = context.binPath();
        if (Files.exists(binPath)) {
            if (!Files.isDirectory(binPath)) {
                try {
                    Files.delete(binPath);
                } catch (IOException e) {
                    return result(List.of("Unable to delete bin file (conflicting with output folder): " + e.getMessage()), 0D);
                }
            } else {
                Processes.launch(OS.Companion.getCURRENT_OS().deleteDirectoryCommand(binPath));
            }
        }
        if (!Files.exists(binPath)) {
            try {
                Files.createDirectory(binPath);
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to create bin directory: " + e.getMessage(), e);
            }
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits;
        try {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(binPath.toFile()));

            compilationUnits = fileManager.getJavaFileObjectsFromPaths(Files.walk(context.getExercise().getRoot().resolve("src"))
                .filter(f -> !Files.isDirectory(f))
                .filter(f -> f.toString().endsWith(".java"))
                .collect(Collectors.toSet()));

        } catch (IOException e) {
            context.compilationFailures.add("Cannot launch compilation");
            return result(List.of("Unable to list root directory: " + e.getClass().getSimpleName() + " " + e.getMessage()), 0D);
        }

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
        boolean success = task.call();
        if (!success) {
            context.compilationFailures.addAll(diagnostics.getDiagnostics().stream().map(d -> "l." + d.getPosition() + ": " + d.getMessage(null)).collect(Collectors.toList()));

            Iterable<? extends JavaFileObject> onlyHelloWorldClass = fileManager.getJavaFileObjectsFromPaths(Set.of(context.getExercise().getRoot().resolve("src/HelloWorld.java")));
            task = compiler.getTask(null, fileManager, diagnostics, null, null, onlyHelloWorldClass);
            success = task.call();
            if (!success) {
                return result(context.compilationFailures, 0D);
            }
        }

        String command = escape(Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java")) + " -cp " + escape(binPath) + " HelloWorld";
        Processes.ProcessResult result = Processes.launch(command, null);

        String expected = "Hello World";

        if (result.getExitCode() != 0) {
            String cause = result.getOutput() != null ? result.getOutput() : result.getCause().getMessage();
            return result(List.of("Cannot start HelloWorld: " + cause), 0D);
        } else if (!easyEquals(result.getOutput(), expected)) {
            return result(List.of("Wrong message, expecting **" + expected + "**, but found: `" + result.getOutput() + '`'), 1D);
        } else {
            return result(List.of(), maxGrade());
        }
    }

    private String escape(Path path) {
        boolean isWin = OS.WINDOWS.isCurrentOs();
        if (isWin) {
            return "\"" + path + "\"";
        } else {
            return path.toString();
        }
    }
}
