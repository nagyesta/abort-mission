package com.github.nagyesta.abortmission.core.outline;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.MissionControl;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;

/**
 * Abstract superclass allowing us to define mission health checks we want to use in our context.
 */
public abstract class MissionOutline {

    /**
     * The name of the shared context.
     */
    public static final String SHARED_CONTEXT = "";

    /**
     * Implementing this method is the recommended way for mission context configuration.
     *
     * @return the name - context consumer pairs which will be used by callers at the beginning of test tuns.
     */
    protected abstract Map<String, Consumer<AbortMissionCommandOps>> defineOutline();

    /**
     * Entry point for context configuration.
     */
    public final void initialBriefing() {
        synchronized (MissionOutlineHolder.NAMED_CONTEXTS) {
            final Map<String, Consumer<AbortMissionCommandOps>> map = defineOutline();
            map.forEach((k, v) -> {
                if (!MissionOutlineHolder.NAMED_CONTEXTS.contains(k)) {
                    if (SHARED_CONTEXT.equals(k)) {
                        MissionControl.createSharedCommandOps(ops -> {
                            v.accept(ops);
                            return ops;
                        });
                    } else {
                        MissionControl.createCommandOps(k, ops -> {
                            v.accept(ops);
                            return ops;
                        });
                    }
                    MissionOutlineHolder.NAMED_CONTEXTS.add(k);
                }
            });
        }
    }

    private static final class MissionOutlineHolder {
        private static final Set<String> NAMED_CONTEXTS = new TreeSet<>();
    }
}
