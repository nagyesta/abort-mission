package com.github.nagyesta.abortmission.booster.jupiter;

import java.util.concurrent.CopyOnWriteArraySet;

public final class ThreadTracker {

    private ThreadTracker() {
        throw new IllegalCallerException("This is a utility class.");
    }
    static final CopyOnWriteArraySet<String> THREADS_USED = new CopyOnWriteArraySet<>();
}
