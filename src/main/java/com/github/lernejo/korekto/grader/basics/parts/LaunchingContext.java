package com.github.lernejo.korekto.grader.basics.parts;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LaunchingContext {

    final List<String> compilationFailures = new ArrayList<>();

    Path binPath;

    final ProcessBuilder processBuilder = new ProcessBuilder();
}
