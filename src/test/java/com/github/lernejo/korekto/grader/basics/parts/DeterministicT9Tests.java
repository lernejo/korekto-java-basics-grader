package com.github.lernejo.korekto.grader.basics.parts;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Set;

class DeterministicT9Tests {

    @Test
    @Disabled("A debug utility")
    void debug_start_word() throws IOException {
        String content = Files.readString(LaunchingContext.classpathToPath("text1.txt"));
        Map<String, Map<String, Integer>> followersByPrefix = Part7Grader.followersByPrefix(content);
        var t9 = Part7Grader.t9(content, "royal", 20);

        for (var word : Set.copyOf(t9)) {
            System.out.println(word + " --> " + followersByPrefix.get(word));
        }
        System.out.println("\n\n" + t9);
    }
}
