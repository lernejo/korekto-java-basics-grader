package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.grader.basics.CloseableProcess;
import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;

import java.io.IOException;
import java.util.List;

public class Part3Grader implements PartGrader {
    @Override
    public String name() {
        return "The loop";
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
        int count = 0;
        try (CloseableProcess process = new CloseableProcess(launchingContext.processBuilder.start())) {
            readOutput(process.process); // optional welcome message

            do {
                writeInput(process.process, "toto\n");
                count++;
            } while (process.process.isAlive() && count < 10);

            writeInput(process.process, "quit\n"); // give it a chance to quit normally
        } catch (IOException | RuntimeException e) {
            return result(List.of("Cannot start Launcher: " + e.getMessage()), 0D);
        }
        if (count != 10) {
            return result(List.of("Expecting looping on unknown command, but could only iterate " + count + " times"), 0D);
        } else {
            return result(List.of(), maxGrade());
        }
    }
}
