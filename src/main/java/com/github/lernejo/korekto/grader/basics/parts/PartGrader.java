package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.Exercise;
import com.github.lernejo.korekto.toolkit.GradePart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;

public interface PartGrader {

    Logger LOGGER = LoggerFactory.getLogger(PartGrader.class);

    String name();

    double maxGrade();

    GradePart grade(Exercise exercise, LaunchingContext launchingContext);

    default GradePart result(List<String> explanations, double grade) {
        return new GradePart(name(), Math.min(Math.max(0, grade), maxGrade()), Double.valueOf(maxGrade()), explanations);
    }

    default String readOutput(Process process) {
        try {
            TimeUnit.MILLISECONDS.sleep(100L);
            StringBuilder sb = new StringBuilder();
            while (process.getInputStream().available() > 0) {
                byte[] bytes = process.getInputStream().readNBytes(process.getInputStream().available());
                sb.append(new String(bytes, StandardCharsets.UTF_8));
            }
            return sb.toString();
        } catch (IOException | InterruptedException e) {
            LOGGER.warn("Unable to read process output: " + e.getMessage());
            return null;
        }
    }

    default void writeInput(Process process, String s) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            writer.write(s);
            writer.flush();
            TimeUnit.MILLISECONDS.sleep(100L);
        } catch (IOException | InterruptedException e) {
            LOGGER.warn("Unable to write to process input: " + e.getMessage());
        }
    }

    default boolean easyEquals(String actual, String expected) {
        String easyExpected = expected.replace("\s", "").toLowerCase();
        return actual != null && actual.replace("\s", "").toLowerCase().contains(easyExpected);
    }

    default Path classpathToPath(String classPath) {
        URL resource = Part5Grader.class.getClassLoader().getResource(classPath);
        try {
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
