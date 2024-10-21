package com.github.lernejo.korekto.grader.basics;

import com.github.lernejo.korekto.grader.basics.parts.*;
import com.github.lernejo.korekto.toolkit.Grader;
import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.PartGrader;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class JavaBasicsGrader implements Grader<LaunchingContext> {

    private final Map<String, List<String>> firstWords = Part7Grader.findDeterministicFirstWords("text1.txt", 20);

    @NotNull
    @Override
    public String name() {
        return "Java 1️⃣0️⃣1️⃣";
    }

    @NotNull
    @Override
    public String slugToRepoUrl(@NotNull String slug) {
        return "https://github.com/" + slug + "/java_exercise_1";
    }

    @NotNull
    @Override
    public LaunchingContext gradingContext(@NotNull GradingConfiguration configuration) {
        return new LaunchingContext(configuration, firstWords);
    }

    @Override
    public boolean needsWorkspaceReset() {
        return true;
    }

    @NotNull
    @Override
    public Collection<PartGrader<LaunchingContext>> graders() {
        return List.of(
            new Part1Grader("Hello World", 2.0D),
            new Part2Grader("Quit and Unknown command", 2.0D),
            new Part3Grader("The loop", 1.0D),
            new Part4Grader("Fibonacci command", 1.0D),
            new Part5Grader("Frequency command", 1.0D),
            new Part6Grader("Refactoring", 4.0D),
            new Part7Grader("Predict command", 2.0D)
        );
    }
}
