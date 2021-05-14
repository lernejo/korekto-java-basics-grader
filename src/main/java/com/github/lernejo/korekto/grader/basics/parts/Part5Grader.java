package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.grader.basics.CloseableProcess;
import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class Part5Grader implements PartGrader {
    @Override
    public String name() {
        return "Frequency command";
    }

    @Override
    public double maxGrade() {
        return 1;
    }

    @Override
    public GradePart grade(Exercise exercise, LaunchingContext launchingContext) {
        if (!launchingContext.compilationFailures.isEmpty()) {
            return result(List.of("Cannot launch program with compilation issues"), 0D);
        }
        Path text1Path = classpathToPath("text1.txt");
        try (CloseableProcess process = new CloseableProcess(launchingContext.processBuilder.start())) {
            readOutput(process.process); // optional welcome message
            writeInput(process.process, "freq\n");
            String freqInvite = readOutput(process.process).trim();
            writeInput(process.process, text1Path.toString() + '\n');
            String freqResult = readOutput(process.process).trim();
            String expected = "the lorem of";
            if (!easyEquals(freqResult, expected)) {
                return result(List.of("Expecting freq command result to be **" + expected + "** but was: `" + freqResult + '`'), 0D);
            }
        } catch (IOException | RuntimeException e) {
            return result(List.of("Cannot start Launcher: " + e.getMessage()), 0D);
        }
        return result(List.of(), maxGrade());
    }
}
