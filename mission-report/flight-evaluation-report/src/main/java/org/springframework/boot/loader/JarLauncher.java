package org.springframework.boot.loader;

import com.github.nagyesta.abortmission.reporting.AbortMissionFlightEvaluationReportApp;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@Slf4j
@Deprecated
public class JarLauncher {

    @Deprecated
    public static void main(final String[] args) {
        log.error("This is the legacy launcher. "
                + "Please use 'com.github.nagyesta.abortmission.reporting.AbortMissionFlightEvaluationReportApp' instead!");
        AbortMissionFlightEvaluationReportApp.main(args);
    }
}
