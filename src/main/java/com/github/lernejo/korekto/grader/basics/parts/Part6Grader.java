package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public class Part6Grader implements PartGrader<LaunchingContext> {

    private static final Set<String> ILLEGAL_COMMAND_NAMES = Set.of("\"fibo\"", "\"freq\"", "\"quit\"", "\"predict\"");

    @Override
    public String name() {
        return "Refactoring";
    }

    @Override
    public Double maxGrade() {
        return 2.0;
    }

    @Override
    public GradePart grade(LaunchingContext context) {
        if (!context.compilationFailures.isEmpty()) {
            return result(List.of("Nothing to analyze given existing compilation issues"), 0D);
        }
        Path launcherPath = context.getExercise().getRoot().resolve("src").resolve("Launcher.java");
        String content;
        try {
            content = Files.readString(launcherPath);
        } catch (IOException e) {
            return result(List.of("Unable to read **src/Launcher.java** file: " + e.getMessage()), 0D);
        }
        if (ILLEGAL_COMMAND_NAMES.stream().anyMatch(content::contains)) {
            return result(List.of("refactoring not performed, there is still references of command names"), 0D);
        } else {
            return result(List.of(), maxGrade());
        }
    }
}
