package com.github.lernejo.korekto.grader.basics;

import com.github.lernejo.korekto.grader.basics.parts.*;
import com.github.lernejo.korekto.toolkit.Grader;
import com.github.lernejo.korekto.toolkit.GradingConfiguration;
import com.github.lernejo.korekto.toolkit.GradingContext;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public class JavaBasicsGrader implements Grader {

    @Override
    public void run(GradingConfiguration gradingConfiguration, GradingContext context) {
        LaunchingContext launchingContext = new LaunchingContext();
        graders().stream()
            .map(g -> g.grade(context.getExercise(), launchingContext))
            .forEach(context.getGradeDetails().getParts()::add);
    }

    private Collection<? extends PartGrader> graders() {
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
}
