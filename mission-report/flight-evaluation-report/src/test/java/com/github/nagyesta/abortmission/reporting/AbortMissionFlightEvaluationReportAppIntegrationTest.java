package com.github.nagyesta.abortmission.reporting;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class AbortMissionFlightEvaluationReportAppIntegrationTest {

    @Autowired
    private AbortMissionFlightEvaluationReportApp app;

    @Test
    void testApplicationContextShouldConfigureSuccessfullyWhenCalled() {
        //given test instance created
        //when injected

        //then
        assertNotNull(app);
    }
}
