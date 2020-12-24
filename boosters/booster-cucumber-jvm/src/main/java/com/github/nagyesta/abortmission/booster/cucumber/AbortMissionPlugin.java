package com.github.nagyesta.abortmission.booster.cucumber;

import com.github.nagyesta.abortmission.core.telemetry.ReportingHelper;
import io.cucumber.messages.Messages;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;

/**
 * A Cucumber plugin implementation helping Abort-Mission to report the collected data.
 */
public class AbortMissionPlugin implements ConcurrentEventListener {

    @Override
    public void setEventPublisher(final EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(Messages.Envelope.class, event -> {
            if (event.hasTestRunFinished()) {
                new ReportingHelper().report();
            }
        });
    }
}
