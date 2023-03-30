package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.InteractiveProcess;

import java.io.IOException;
import java.util.List;

public class Part3Grader implements PartGrader<LaunchingContext> {
    @Override
    public String name() {
        return "The loop";
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
        int count = 0;
        try (InteractiveProcess process = new InteractiveProcess(context.startLauncherProgram())) {
            process.read(); // optional welcome message
            if (!process.getProcess().isAlive()) {
                return result(List.of("Process exited prematurely"), 0D);
            }
            do {
                process.write("toto\n");
                count++;
            } while (process.getProcess().isAlive() && count < 10);

            process.write("quit\n"); // give it a chance to quit normally
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
