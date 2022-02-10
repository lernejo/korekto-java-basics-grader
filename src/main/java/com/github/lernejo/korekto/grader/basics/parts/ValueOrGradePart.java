package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;

record ValueOrGradePart<T>(T value, GradePart gradePart) {

    static <T> ValueOrGradePart<T> value(T value) {
        return new ValueOrGradePart<>(value, null);
    }

    static <T> ValueOrGradePart<T> gradePart(GradePart gradePart) {
        return new ValueOrGradePart<>(null, gradePart);
    }

    boolean isGradePart() {
        return gradePart != null;
    }
}
