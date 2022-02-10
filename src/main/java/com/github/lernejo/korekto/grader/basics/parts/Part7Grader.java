package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.InteractiveProcess;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import static com.github.lernejo.korekto.grader.basics.parts.LaunchingContext.classpathToPath;
import static com.github.lernejo.korekto.grader.basics.parts.LaunchingContext.easyEquals;

public class Part7Grader implements PartGrader<LaunchingContext> {
    @Override
    public String name() {
        return "Predict command";
    }

    @Override
    public Double maxGrade() {
        return 3.0;
    }

    @Override
    public GradePart grade(LaunchingContext context) {
        if (!context.compilationFailures.isEmpty()) {
            return result(List.of("Cannot launch program with compilation issues"), 0D);
        }
        Path text1Path = classpathToPath("text1.txt");
        try (InteractiveProcess process = new InteractiveProcess(context.startLauncherProgram())) {
            process.read(); // optional welcome message
            process.write("predict\n");
            process.read(); // predict path invite
            process.write(text1Path.toString() + '\n');
            process.read(); // predict word invite
            process.write("The\n");
            String predictResult = process.read().trim();
            String expected = "the internet tend to make a reader will be distracted by the internet tend to make a reader will be";
            if (!easyEquals(predictResult, expected)) {
                return result(List.of("Expecting predict command result to be **" + expected + "** but was: `" + predictResult + '`'), 0D);
            }
        } catch (IOException | RuntimeException e) {
            return result(List.of("Cannot start Launcher: " + e.getMessage()), 0D);
        }
        return result(List.of(), maxGrade());
    }
}
