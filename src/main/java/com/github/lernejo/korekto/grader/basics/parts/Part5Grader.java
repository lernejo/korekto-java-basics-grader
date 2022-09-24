package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.InteractiveProcess;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.lernejo.korekto.grader.basics.parts.LaunchingContext.classpathToPath;

public class Part5Grader implements PartGrader<LaunchingContext> {
    @Override
    public String name() {
        return "Frequency command";
    }

    @Override
    public Double maxGrade() {
        return 1.0;
    }

    @Override
    public GradePart grade(LaunchingContext context) {
        if (!context.compilationFailures.isEmpty()) {
            return result(List.of("Cannot launch program with compilation issues"), 0D);
        }
        Path text1Path = classpathToPath("text1.txt");
        try (InteractiveProcess process = new InteractiveProcess(context.startLauncherProgram())) {
            process.read(); // optional welcome message
            process.write("freq\n");
            process.read(); // freq invite for entering a file path
            process.write(text1Path.toString() + '\n');
            String freqResult = process.read().trim().toLowerCase();
            Set<String> expected = Set.of("the", "lorem", "of");
            if (!expected.stream().allMatch(expected::contains)) {
                return result(List.of("Expecting freq command result to contain **" + expected.stream().collect(Collectors.joining(", ")) + "** but was: `" + freqResult + '`'), 0D);
            }
        } catch (IOException | RuntimeException e) {
            return result(List.of("Cannot start Launcher: " + e.getMessage()), 0D);
        }
        return result(List.of(), maxGrade());
    }
}
