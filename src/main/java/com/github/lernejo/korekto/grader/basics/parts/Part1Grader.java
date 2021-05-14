package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;

import javax.tools.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Part1Grader implements PartGrader {
    @Override
    public String name() {
        return "Hello World";
    }

    @Override
    public double maxGrade() {
        return 2D;
    }

    @Override
    public GradePart grade(Exercise exercise, LaunchingContext launchingContext) {
        launchingContext.binPath = exercise.getRoot().resolve("bin");
        if (Files.exists(launchingContext.binPath) && !Files.isDirectory(launchingContext.binPath)) {
            try {
                Files.delete(launchingContext.binPath);
            } catch (IOException e) {
                return result(List.of("Unable to delete bin file (conflicting with output folder): " + e.getMessage()), 0D);
            }
        }
        if (!Files.exists(launchingContext.binPath)) {
            try {
                Files.createDirectory(launchingContext.binPath);
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to create bin directory: " + e.getMessage(), e);
            }
        }
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<? extends JavaFileObject> compilationUnits;
        try {
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, List.of(launchingContext.binPath.toFile()));

            compilationUnits = fileManager.getJavaFileObjectsFromPaths(Files.walk(exercise.getRoot().resolve("src"))
                .filter(f -> !Files.isDirectory(f))
                .filter(f -> f.toString().endsWith(".java"))
                .collect(Collectors.toSet()));

        } catch (IOException e) {
            launchingContext.compilationFailures.add("Cannot launch compilation");
            return result(List.of("Unable to list root directory: " + e.getClass().getSimpleName() + " " + e.getMessage()), 0D);
        }

        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnits);
        boolean success = task.call();
        if (!success) {
            launchingContext.compilationFailures.addAll(diagnostics.getDiagnostics().stream().map(d -> "l." + d.getPosition() + ": " + d.getMessage(null)).collect(Collectors.toList()));

            Iterable<? extends JavaFileObject> onlyHelloWorldClass = fileManager.getJavaFileObjectsFromPaths(Set.of(exercise.getRoot().resolve("src/HelloWorld.java")));
            task = compiler.getTask(null, fileManager, diagnostics, null, null, onlyHelloWorldClass);
            success = task.call();
            if (!success) {
                return result(launchingContext.compilationFailures, 0D);
            }
        }

        ProcessBuilder processBuilder = new ProcessBuilder(Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java").toString(), "-cp", launchingContext.binPath.toString(), "HelloWorld");
        final Process process;
        String result;
        try {
            process = processBuilder.start();
            try {
                BufferedReader reader =
                    new BufferedReader(new InputStreamReader(process.getInputStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                    builder.append(System.getProperty("line.separator"));
                }
                result = builder.toString();
            } finally {
                process.destroyForcibly();
            }
        } catch (IOException | RuntimeException e) {
            return result(List.of("Cannot start HelloWorld: " + e.getMessage()), 0D);
        }

        String expected = "Hello World";
        if (!easyEquals(result, expected)) {
            return result(List.of("Wrong message, expecting **" + expected + "**, but found: `" + result + '`'), 1D);
        }

        return result(List.of(), maxGrade());
    }
}
