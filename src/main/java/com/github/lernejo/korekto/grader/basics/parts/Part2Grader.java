package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.grader.basics.CloseableProcess;
import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Part2Grader implements PartGrader {
    @Override
    public String name() {
        return "Quit and Unknown command";
    }

    @Override
    public double maxGrade() {
        return 2D;
    }

    @Override
    public GradePart grade(Exercise exercise, LaunchingContext launchingContext) {
        if (!launchingContext.compilationFailures.isEmpty()) {
            return result(List.of("Cannot launch program with compilation issues"), 0D);
        }
        launchingContext.processBuilder.command(Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java").toString(), "-cp", launchingContext.binPath.toString(), "Launcher");
        ValueOrGradePart<String> unknownCommandOutput = getUnknownCommandOutput(launchingContext);
        if (unknownCommandOutput.isGradePart()) {
            return unknownCommandOutput.gradePart;
        }
        ValueOrGradePart<Boolean> quitCommandBehavior = getQuitCommandBehavior(launchingContext);
        if (quitCommandBehavior.isGradePart()) {
            return quitCommandBehavior.gradePart;
        }
        List<String> errors = new ArrayList<>();
        if (!easyEquals(unknownCommandOutput.value, "Unknown command")) {
            errors.add("Expected error message **Unknown command** when entering an unknown command, but was: `" + unknownCommandOutput.value + '`');
        }
        if (quitCommandBehavior.value) {
            errors.add("Program does not quit when entering `quit` command");
        }
        return result(errors, 2D - errors.size());
    }

    private ValueOrGradePart<String> getUnknownCommandOutput(LaunchingContext launchingContext) {
        ValueOrGradePart<String> unknownCommandOutput;
        try (CloseableProcess process = new CloseableProcess(launchingContext.processBuilder.start())) {
            readOutput(process.process); // optional welcome message
            writeInput(process.process, "toto\n");
            unknownCommandOutput = ValueOrGradePart.value(readOutput(process.process));
            if (process.process.isAlive()) {
                writeInput(process.process, "quit\n"); // give it a chance to quit normally
            }
        } catch (IOException | RuntimeException e) {
            unknownCommandOutput = ValueOrGradePart.gradePart(result(List.of("Cannot start Launcher: " + e.getMessage()), 0D));
        }
        return unknownCommandOutput;
    }

    private ValueOrGradePart<Boolean> getQuitCommandBehavior(LaunchingContext launchingContext) {
        ValueOrGradePart<Boolean> unknownCommandOutput;
        try (CloseableProcess process = new CloseableProcess(launchingContext.processBuilder.start())) {
            readOutput(process.process); // optional welcome message
            writeInput(process.process, "quit\n");
            unknownCommandOutput = ValueOrGradePart.value(process.process.isAlive());
        } catch (IOException | RuntimeException e) {
            unknownCommandOutput = ValueOrGradePart.gradePart(result(List.of("Cannot start Launcher: " + e.getMessage()), 0D));
        }
        return unknownCommandOutput;
    }
}
