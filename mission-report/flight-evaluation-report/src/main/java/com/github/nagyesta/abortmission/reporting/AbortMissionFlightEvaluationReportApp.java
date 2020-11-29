package com.github.nagyesta.abortmission.reporting;

import com.github.nagyesta.abortmission.reporting.controller.ConversionController;
import com.github.nagyesta.abortmission.reporting.exception.RenderException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class AbortMissionFlightEvaluationReportApp {

    public static void main(final String[] args) throws RenderException {
        SpringApplication.run(AbortMissionFlightEvaluationReportApp.class, args)
                .getBean(ConversionController.class).convert();
    }
}
