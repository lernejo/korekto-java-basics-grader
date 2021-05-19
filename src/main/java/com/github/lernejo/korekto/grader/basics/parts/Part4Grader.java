package com.github.lernejo.korekto.grader.basics.parts;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import com.github.lernejo.korekto.grader.basics.CloseableProcess;
import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;

public class Part4Grader implements PartGrader {

    private final Random random = new Random();

    @Override
    public String name() {
        return "Fibonacci command";
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
        try (CloseableProcess process = new CloseableProcess(launchingContext.processBuilder.start())) {
            readOutput(process.process); // optional welcome message
            writeInput(process.process, "fibo\n");
            String fiboInvite = readOutput(process.process).trim();

            int n = random.nextInt(10) + 3;
            writeInput(process.process, String.valueOf(n) + '\n');
            String fiboResultRaw = readOutput(process.process).trim();
            int expectedResult = fibo(n);
            List<String> error = List.of("Expecting result of fibo with N=" + n + " to be **" + expectedResult + "** but was: `" + fiboResultRaw + '`');
            try {
                int fiboResult = Integer.parseInt(fiboResultRaw);
                if (!fiboResultRaw.contains(String.valueOf(fiboResult))) {
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
