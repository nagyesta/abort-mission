package com.github.nagyesta.abortmission.core.outline;

import com.github.nagyesta.abortmission.core.AbortMissionCommandOps;
import com.github.nagyesta.abortmission.core.AbortMissionGlobalConfiguration;
import com.github.nagyesta.abortmission.core.MissionControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

/**
 * Abstract superclass allowing us to define mission health checks we want to use in our context.
 */
public abstract class MissionOutline {

    private static final Logger LOGGER = LoggerFactory.getLogger(MissionOutline.class);
    /**
     * The name of the shared context.
     */
    public static final String SHARED_CONTEXT = "";
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * Default constructor calling the global config configuration method.
     */
    protected MissionOutline() {
        overrideGlobalConfig(MissionControl.globalConfiguration());
    }

    /**
     * Override this method to configure the global config.
     *
     * @param config The global config.
     */
    protected void overrideGlobalConfig(final AbortMissionGlobalConfiguration config) {
        //override this method to configure the global config
    }

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
        LOCK.lock();
        try {
            final Map<String, Consumer<AbortMissionCommandOps>> map = defineOutline();
            map.forEach((k, v) -> {
                if (!MissionOutlineHolder.NAMED_CONTEXTS.contains(k)) {
                    LOGGER.debug("Configuring mission outline for context: {}", Optional.of(k)
                            .filter(anObject -> !SHARED_CONTEXT.equals(anObject))
                            .orElse("- SHARED -"));
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
        } finally {
            LOCK.unlock();
        }
    }

    private static final class MissionOutlineHolder {
        private static final Set<String> NAMED_CONTEXTS = new TreeSet<>();
    }
}
