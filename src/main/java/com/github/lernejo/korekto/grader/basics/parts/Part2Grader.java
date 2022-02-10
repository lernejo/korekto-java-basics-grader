package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.InteractiveProcess;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.github.lernejo.korekto.grader.basics.parts.LaunchingContext.easyEquals;

public class Part2Grader implements PartGrader<LaunchingContext> {
    @Override
    public String name() {
        return "Quit and Unknown command";
    }

    @Override
    public Double maxGrade() {
        return 2D;
    }

    @Override
    public GradePart grade(LaunchingContext context) {
        if (!context.compilationFailures.isEmpty()) {
            return result(List.of("Cannot launch program with compilation issues"), 0D);
        }
        ValueOrGradePart<String> unknownCommandOutput = getUnknownCommandOutput(context);
        if (unknownCommandOutput.isGradePart()) {
            return unknownCommandOutput.gradePart();
        }
        ValueOrGradePart<Boolean> quitCommandBehavior = getQuitCommandBehavior(context);
        if (quitCommandBehavior.isGradePart()) {
            return quitCommandBehavior.gradePart();
        }
        List<String> errors = new ArrayList<>();
        if (!easyEquals(unknownCommandOutput.value(), "Unknown command")) {
            errors.add("Expected error message **Unknown command** when entering an unknown command, but was: `" + unknownCommandOutput.value() + '`');
        }
        if (quitCommandBehavior.value()) {
            errors.add("Program does not quit when entering `quit` command");
        }
        return result(errors, 2D - errors.size());
    }

    private ValueOrGradePart<String> getUnknownCommandOutput(LaunchingContext launchingContext) {
        ValueOrGradePart<String> unknownCommandOutput;
        try (InteractiveProcess process = new InteractiveProcess(launchingContext.startLauncherProgram())) {
            process.read(); // optional welcome message
            process.write("toto\n");
            unknownCommandOutput = ValueOrGradePart.value(process.read());
            if (process.getProcess().isAlive()) {
                process.write("quit\n"); // give it a chance to quit normally
            }
        } catch (IOException | RuntimeException e) {
            unknownCommandOutput = ValueOrGradePart.gradePart(result(List.of("Cannot start Launcher: " + e.getMessage()), 0D));
        }
        return unknownCommandOutput;
    }

    private ValueOrGradePart<Boolean> getQuitCommandBehavior(LaunchingContext launchingContext) {
        ValueOrGradePart<Boolean> unknownCommandOutput;
        try (InteractiveProcess process = new InteractiveProcess(launchingContext.startLauncherProgram())) {
            process.read(); // optional welcome message
            process.write("quit\n");
            unknownCommandOutput = ValueOrGradePart.value(process.getProcess().isAlive());
        } catch (IOException | RuntimeException e) {
            unknownCommandOutput = ValueOrGradePart.gradePart(result(List.of("Cannot start Launcher: " + e.getMessage()), 0D));
        }
        return unknownCommandOutput;
    }
}
