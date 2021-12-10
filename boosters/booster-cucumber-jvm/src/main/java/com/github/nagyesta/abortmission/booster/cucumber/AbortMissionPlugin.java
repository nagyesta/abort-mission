package com.github.nagyesta.abortmission.booster.cucumber;

import com.github.nagyesta.abortmission.core.telemetry.ReportingHelper;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.TestRunFinished;

/**
 * A Cucumber plugin implementation helping Abort-Mission to report the collected data.
 */
public class AbortMissionPlugin implements ConcurrentEventListener {

    @Override
    public void setEventPublisher(final EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestRunFinished.class, event -> new ReportingHelper().report());
    }
}
