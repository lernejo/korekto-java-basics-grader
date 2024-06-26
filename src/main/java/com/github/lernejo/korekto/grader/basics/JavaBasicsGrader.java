package com.github.lernejo.korekto.grader.basics;

import com.github.lernejo.korekto.grader.basics.parts.*;
import com.github.lernejo.korekto.toolkit.*;
import com.github.lernejo.korekto.toolkit.misc.HumanReadableDuration;
import com.github.lernejo.korekto.toolkit.thirdparty.github.GitHubNature;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class JavaBasicsGrader implements Grader<LaunchingContext> {

    private final Logger logger = LoggerFactory.getLogger(JavaBasicsGrader.class);

    private final Map<String, List<String>> firstWords;

    public JavaBasicsGrader() {
        firstWords = Part7Grader.findDeterministicFirstWords("text1.txt", 20);
    }

    @Override
    public String name() {
        return "Java 1\uFE0F⃣ 0\uFE0F⃣1\uFE0F⃣ ";
    }

    @Override
    public boolean needsWorkspaceReset() {
        return true;
    }

    @Override
    public void run(LaunchingContext context) {
        context.getGradeDetails().getParts().addAll(grade(context));
    }

    private Collection<? extends GradePart> grade(LaunchingContext context) {
        Optional<GitHubNature> gitHubNature = context.getExercise().lookupNature(GitHubNature.class);
        if (gitHubNature.isPresent()) {
            try {
                System.out.println(gitHubNature.get().getContext().getGitHub().getRateLimit());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return graders().stream()
            .map(g -> applyPartGrader(context, g))
            .collect(Collectors.toList());
    }

    private GradePart applyPartGrader(LaunchingContext context, PartGrader<LaunchingContext> g) {
        long startTime = System.currentTimeMillis();
        try {
            return g.grade(context);
        } finally {
            logger.debug(g.name() + " in " + HumanReadableDuration.toString(System.currentTimeMillis() - startTime));
        }
    }

    private Collection<PartGrader<LaunchingContext>> graders() {
        return List.of(
            new Part1Grader(),
            new Part2Grader(),
            new Part3Grader(),
            new Part4Grader(),
            new Part5Grader(),
            new Part6Grader(),
            new Part7Grader()
        );
    }

    @Override
    public Instant deadline(GradingContext context) {
        return null;
    }

    @Override
    public String slugToRepoUrl(String slug) {
        return "https://github.com/" + slug + "/java_exercise_1";
    }

    @NotNull
    @Override
    public LaunchingContext gradingContext(@NotNull GradingConfiguration configuration) {
        return new LaunchingContext(configuration, firstWords);
    }
}
