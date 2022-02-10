package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.GradingContext;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LaunchingContext extends GradingContext {

    final List<String> compilationFailures = new ArrayList<>();

    public LaunchingContext(GradingConfiguration configuration) {
        super(configuration);
    }

    Path binPath() {
        return getExercise().getRoot().resolve("bin");
    }

    Process startLauncherProgram() throws IOException {
        return new ProcessBuilder().command(
            Paths.get(System.getProperty("java.home")).resolve("bin").resolve("java").toString(),
            "-cp",
            binPath().toString(),
            "Launcher")
            .start();
    }

    static boolean easyEquals(String actual, String expected) {
        String easyExpected = expected.replace("\s", "").toLowerCase();
        return actual != null && actual.replace("\s", "").toLowerCase().contains(easyExpected);
    }

    static Path classpathToPath(String classPath) {
        URL resource = Part5Grader.class.getClassLoader().getResource(classPath);
        try {
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
