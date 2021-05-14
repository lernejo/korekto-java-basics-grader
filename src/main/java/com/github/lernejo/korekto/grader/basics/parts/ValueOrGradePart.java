package com.github.lernejo.korekto.grader.basics.parts;

import com.github.lernejo.korekto.toolkit.GradePart;

class ValueOrGradePart<T> {
    public final T value;
    public final GradePart gradePart;

    private ValueOrGradePart(T value, GradePart gradePart) {
        this.value = value;
        this.gradePart = gradePart;
    }

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
