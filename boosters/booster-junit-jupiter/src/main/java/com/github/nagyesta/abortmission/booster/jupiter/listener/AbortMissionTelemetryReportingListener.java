package com.github.nagyesta.abortmission.booster.jupiter.listener;

import com.github.nagyesta.abortmission.core.telemetry.ReportingHelper;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;

public class AbortMissionTelemetryReportingListener implements TestExecutionListener {

    @Override
    public void testPlanExecutionFinished(final TestPlan testPlan) {
        new ReportingHelper().report();
    }
}
