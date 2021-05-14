package com.github.lernejo.korekto.grader.basics;

public class CloseableProcess implements AutoCloseable {

    public final Process process;

    public CloseableProcess(Process process) {
        this.process = process;
    }

    @Override
    public void close() {
        process.destroyForcibly();
    }
}
