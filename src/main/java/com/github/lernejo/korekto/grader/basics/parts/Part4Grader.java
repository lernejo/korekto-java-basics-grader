package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.GradingContext;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.InteractiveProcess;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public record Part4Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {

    @NotNull
    @Override
    public GradePart grade(LaunchingContext context) {
        if (!context.compilationFailures.isEmpty()) {
            return result(List.of("Cannot launch program with compilation issues"), 0D);
        }
        try (InteractiveProcess process = new InteractiveProcess(context.startLauncherProgram())) {
            process.read(); // optional welcome message
            if (!process.getProcess().isAlive()) {
                return result(List.of("Process exited prematurely"), 0D);
            }
            process.write("fibo\n");
            String fiboInvite = Objects.requireNonNullElse(process.read(), "").trim();

            int n = GradingContext.getRandomSource().nextInt(10) + 3;
            process.write(String.valueOf(n) + '\n');
            String fiboResultRaw = Objects.requireNonNullElse(process.read(), "").trim();
            int expectedResult = fibo(n);
            List<String> error = List.of("Expecting result of fibo with N=" + n + " to be **" + expectedResult + "** but was: `" + fiboResultRaw + '`');
            try {
                if (!fiboResultRaw.contains(String.valueOf(expectedResult))) {
                    return result(error, 0D);
                } else if (fiboInvite.isBlank()) {
                    return result(List.of("No message prompted in fibo asking for the user to enter a number"), maxGrade / 2);
                } else {
                    return result(List.of(), maxGrade);
                }
            } catch (NumberFormatException e) {
                return result(error, 0D);
            }

        } catch (IOException | RuntimeException e) {
            return result(List.of("Cannot start Launcher: " + e.getMessage()), 0D);
        }
    }

    private int fibo(int n) {
        if (n == 0 || n == 1) {
            return n;
        }
        return fibo(n - 1) + fibo(n - 2);
    }
}
