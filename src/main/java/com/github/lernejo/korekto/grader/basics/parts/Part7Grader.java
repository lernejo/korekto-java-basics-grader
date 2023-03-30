package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;
import com.github.lernejo.korekto.toolkit.PartGrader;
import com.github.lernejo.korekto.toolkit.misc.InteractiveProcess;
import com.github.lernejo.korekto.toolkit.misc.ThrowingFunction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.github.lernejo.korekto.grader.basics.parts.LaunchingContext.classpathToPath;
import static com.github.lernejo.korekto.grader.basics.parts.LaunchingContext.easyEquals;

public class Part7Grader implements PartGrader<LaunchingContext> {


    @Override
    public String name() {
        return "Predict command";
    }

    @Override
    public Double maxGrade() {
        return 3.0;
    }

    @Override
    public GradePart grade(LaunchingContext context) {
        if (!context.compilationFailures.isEmpty()) {
            return result(List.of("Cannot launch program with compilation issues"), 0D);
        }
        Path text1Path = classpathToPath("text1.txt");
        var firstWord = context.chooseFirstWord();
        String expected = firstWord.getValue().stream().collect(Collectors.joining(" "));
        try (InteractiveProcess process = new InteractiveProcess(context.startLauncherProgram())) {
            process.read(); // optional welcome message
            if (!process.getProcess().isAlive()) {
                return result(List.of("Process exited prematurely"), 0D);
            }
            process.write("predict\n");
            process.read(); // predict path invite
            process.write(text1Path.toString() + '\n');
            process.read(); // predict word invite
            process.write(firstWord.getKey() + "\n");
            String predictResult = Objects.requireNonNullElse(process.read(), "").trim();

            if (!easyEquals(predictResult, expected)) {
                return result(List.of("Expecting predict command result to be **" + expected + "** but was: `" + predictResult + '`'), 0D);
            }
        } catch (IOException | RuntimeException e) {
            return result(List.of("Cannot start Launcher: " + e.getMessage()), 0D);
        }
        return result(List.of(), maxGrade());
    }

    public static Map<String, List<String>> findDeterministicFirstWords(String file, int sentenceLength) {
        String content = ThrowingFunction.sneaky(x -> Files.readString(LaunchingContext.classpathToPath(file))).apply(null);
        var tokens = Set.copyOf(tokenize(content));
        Map<String, Map<String, Integer>> followersByPrefix = followersByPrefix(content);

        return tokens.stream()
            .filter(t -> isDeterministicFirstWord(content, t, followersByPrefix, sentenceLength))
            .collect(Collectors.toMap(Function.identity(), t -> t9(content, t, sentenceLength)));
    }

    private static boolean isDeterministicFirstWord(String content, String firstWord, Map<String, Map<String, Integer>> followersByPrefix, int length) {
        var t9 = t9(content, firstWord, length);

        return Set.copyOf(t9).stream().allMatch(w -> {
            Map<String, Integer> occurrences = followersByPrefix.get(w);
            if (occurrences == null || occurrences.size() < 2) {
                return true;
            }
            Iterator<Integer> weights = occurrences.values().iterator();
            Integer first = weights.next();
            Integer second = weights.next();
            return first > second;
        });
    }

    static List<String> t9(String content, String firstWord, int sentenceLength) {
        var followersByPrefix = followersByPrefix(content);
        List<String> sentence = new ArrayList<>();
        var currentWord = firstWord.toLowerCase(Locale.ROOT);
        sentence.add(currentWord);
        for (var i = 0; i < sentenceLength - 1; i++) {
            Map<String, Integer> occurrences = followersByPrefix.get(currentWord);
            if (occurrences == null) {
                break;
            }
            currentWord = occurrences.keySet().iterator().next();
            sentence.add(currentWord);
        }
        return sentence;
    }

    private static List<String> tokenize(String content) {
        var tokens = content
            .replaceAll("\\W", " ")
            .toLowerCase(Locale.ROOT)
            .split(" ");
        return Arrays.stream(tokens)
            .filter(s -> !s.isBlank())
            .toList();
    }

    static Map<String, Map<String, Integer>> followersByPrefix(String content) {
        record KV(String k, Map<String, Integer> v) {
        }
        List<String> nonBlankTokens = tokenize(content);
        var potentialWordsByPrefix = potentialWordsByPrefix(nonBlankTokens);
        return potentialWordsByPrefix.entrySet()
            .stream()
            .map(e -> new KV(e.getKey(), freq(e.getValue())))
            .collect(Collectors.toMap(KV::k, KV::v));
    }

    private static Map<String, Integer> freq(List<String> words) {
        Map<String, Integer> freq = new LinkedHashMap<>();
        words.stream()
            .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
            .entrySet()
            .stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .forEach(e -> freq.put(e.getKey(), e.getValue().intValue()));
        return freq;
    }

    private static Map<String, List<String>> potentialWordsByPrefix(List<String> tokens) {
        Map<String, List<String>> potentialWordsByPrefix = new HashMap<>();
        for (var i = 0; i < tokens.size() - 1; i++) {
            potentialWordsByPrefix.computeIfAbsent(tokens.get(i), k -> new ArrayList<>()).add(tokens.get(i + 1));
        }
        return potentialWordsByPrefix;
    }
}
