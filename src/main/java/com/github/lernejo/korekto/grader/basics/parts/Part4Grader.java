package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.InteractiveProcess;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class Part4Grader implements PartGrader<LaunchingContext> {

    private final Random random = new Random();

    @Override
    public String name() {
        return "Fibonacci command";
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
        try (InteractiveProcess process = new InteractiveProcess(context.startLauncherProgram())) {
            process.read(); // optional welcome message
            process.write("fibo\n");
            String fiboInvite = process.read().trim();

            int n = random.nextInt(10) + 3;
            process.write(String.valueOf(n) + '\n');
            String fiboResultRaw = process.read().trim();
            int expectedResult = fibo(n);
            List<String> error = List.of("Expecting result of fibo with N=" + n + " to be **" + expectedResult + "** but was: `" + fiboResultRaw + '`');
            try {
                if (!fiboResultRaw.contains(String.valueOf(expectedResult))) {
                    return result(error, 0D);
                } else {
                    return result(List.of(), maxGrade() - (fiboInvite.isBlank() ? 0.5D : 0D));
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
