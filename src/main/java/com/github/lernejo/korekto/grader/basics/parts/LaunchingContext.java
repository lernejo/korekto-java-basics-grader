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
import java.util.Map;

public class LaunchingContext extends GradingContext {

    final List<String> compilationFailures = new ArrayList<>();
    final Map<String, List<String>> firstWords;

    public LaunchingContext(GradingConfiguration configuration, Map<String, List<String>> firstWords) {
        super(configuration);
        this.firstWords = firstWords;
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

    public static Path classpathToPath(String classPath) {
        URL resource = LaunchingContext.class.getClassLoader().getResource(classPath);
        try {
            return Paths.get(resource.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    Map.Entry<String, List<String>> chooseFirstWord() {
        return List.copyOf(firstWords.entrySet()).get(getRandomSource().nextInt(firstWords.size()));
    }
}
