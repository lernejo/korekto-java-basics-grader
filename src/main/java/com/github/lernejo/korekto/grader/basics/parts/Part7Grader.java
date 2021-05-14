package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.grader.basics.CloseableProcess;
import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Part7Grader implements PartGrader {
    @Override
    public String name() {
        return "Predict command";
    }

    @Override
    public double maxGrade() {
        return 3;
    }

    @Override
    public GradePart grade(Exercise exercise, LaunchingContext launchingContext) {
        if (!launchingContext.compilationFailures.isEmpty()) {
            return result(List.of("Cannot launch program with compilation issues"), 0D);
        }
        Path text1Path = classpathToPath("text1.txt");
        try (CloseableProcess process = new CloseableProcess(launchingContext.processBuilder.start())) {
            readOutput(process.process); // optional welcome message
            writeInput(process.process, "predict\n");
            String predictInvite = readOutput(process.process).trim();
            writeInput(process.process, text1Path.toString() + '\n');
            String predictWordInvite = readOutput(process.process).trim();
            writeInput(process.process, "The\n");
            String predictResult = readOutput(process.process).trim();
            String expected = "the internet tend to using lorem ipsum is that a search for lorem ipsum is that a search for lorem";
            if (!easyEquals(predictResult, expected)) {
                return result(List.of("Expecting predict command result to be **" + expected + "** but was: `" + predictResult + '`'), 0D);
            }
        } catch (IOException | RuntimeException e) {
            return result(List.of("Cannot start Launcher: " + e.getMessage()), 0D);
        }
        return result(List.of(), maxGrade());
    }
}
