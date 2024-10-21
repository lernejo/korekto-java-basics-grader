package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.InteractiveProcess;
import com.github.lernejo.korekto.toolkit.misc.ThrowingFunction;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.lernejo.korekto.grader.basics.parts.LaunchingContext.classpathToPath;

public record Part5Grader(String name, Double maxGrade) implements PartGrader<LaunchingContext> {

    @NotNull
    @Override
    public GradePart grade(LaunchingContext context) {
        if (!context.compilationFailures.isEmpty()) {
            return result(List.of("Cannot launch program with compilation issues"), 0D);
        }
        Path text1Path = classpathToPath("text1.txt");
        Map<String, Integer> occurrencesByWord = wordOccurrences(text1Path, 6);
        try (InteractiveProcess process = new InteractiveProcess(context.startLauncherProgram())) {
            process.read(); // optional welcome message
            if (!process.getProcess().isAlive()) {
                return result(List.of("Process exited prematurely"), 0D);
            }
            process.write("freq\n");
            process.read(); // freq invite for entering a file path
            process.write(text1Path.toString() + '\n');
            String freqResult = Objects.requireNonNullElse(process.read(), "").trim().toLowerCase();
            Collection<String> expected = take(occurrencesByWord.keySet(), 3);
            if (!expected.stream().allMatch(freqResult::contains)) {
                return result(List.of("Expecting freq command result to contain **" + String.join(", ", expected) + "** but was: `" + freqResult + "`. 6 most occurring words being " + occurrencesByWord), 0D);
            }
        } catch (IOException | RuntimeException e) {
            return result(List.of("Cannot start Launcher: " + e.getMessage()), 0D);
        }
        return result(List.of(), maxGrade);
    }

    private List<String> take(Collection<String> values, int max) {
        int iter = 0;
        List<String> l = new ArrayList<>();
        for (String value : values) {
            iter++;
            if (iter > max) {
                break;
            }
            l.add(value);
        }
        return l;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Integer> wordOccurrences(Path path, int limit) {
        String content = ThrowingFunction.sneaky((Path p) -> Files.readString(p)).apply(path);

        Stream<Map.Entry<String, Long>> stream = Arrays.stream(content
                .replaceAll("\\W", " ")
                .toLowerCase(Locale.ROOT)
                .split(" "))
            .filter(s -> !s.isBlank())
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(limit);
        Collector<Map.Entry<String, Long>, ?, Map> collector = Collectors.toMap(
            Map.Entry::getKey,
            e -> e.getValue().intValue(),
            Integer::sum,
            LinkedHashMap::new);
        return stream.collect(collector);
    }
}
