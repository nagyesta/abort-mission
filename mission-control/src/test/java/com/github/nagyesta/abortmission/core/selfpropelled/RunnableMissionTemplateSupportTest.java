package com.github.nagyesta.abortmission.core.selfpropelled;

import com.github.nagyesta.abortmission.core.annotation.AnnotationContextEvaluator;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressAbortDecisions
class RunnableMissionTemplateSupportTest extends AbstractMissionTemplateSupportTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSimpleRunnableShouldLogLifeCycleEventsWhenCalled(final boolean fail) {
        //given
        AnnotationContextEvaluator.shared().findAndApplyLaunchPlanDefinition(RunnableMissionTemplateSupportTest.class);

        final MissionHealthCheckEvaluator evaluator = getRelevantEvaluator(fail);
        final int countdownStart = evaluator.getCountdownStatistics().getTotal();
        final int countdownComplete = evaluator.getCountdownStatistics().getSucceeded();
        final int countdownFail = evaluator.getCountdownStatistics().getFailed();
        final int missionSuccess = evaluator.getMissionStatistics().getSucceeded();

        final RunnableMissionTemplateSupport<Boolean> underTest = new RunnableMissionTemplateSupport<Boolean>(
                MissionOutlineDefinition.SELF_PROPELLED_RUNNABLE + fail,
                this.getClass(), AbstractMissionTemplateSupportTest::abort) {
            @Override
            public Supplier<Boolean> preLaunchPreparationSupplier() {
                return () -> {
                    //no change in advance
                    Assertions.assertEquals(countdownStart, evaluator.getCountdownStatistics().getTotal());
                    Assertions.assertEquals(countdownComplete, evaluator.getCountdownStatistics().getSucceeded());
                    Assertions.assertEquals(countdownFail, evaluator.getMissionStatistics().getFailed());
                    Assertions.assertEquals(missionSuccess, evaluator.getMissionStatistics().getSucceeded());
                    if (fail) {
                        throw new IllegalStateException();
                    }
                    return false;
                };
            }

            @Override
            public Consumer<Boolean> missionPayloadConsumer() {
                return v -> {
                };
            }
        };

        //when
        try {
            underTest.run();
        } catch (final IllegalStateException ignore) {

        }

        //then
        Assertions.assertEquals(countdownStart + 1, evaluator.getCountdownStatistics().getTotal());
        if (fail) {
            Assertions.assertEquals(countdownComplete, evaluator.getCountdownStatistics().getSucceeded());
            Assertions.assertEquals(countdownFail + 1, evaluator.getCountdownStatistics().getFailed());
            Assertions.assertEquals(missionSuccess, evaluator.getMissionStatistics().getSucceeded());
        } else {
            Assertions.assertEquals(countdownComplete + 1, evaluator.getCountdownStatistics().getSucceeded());
            Assertions.assertEquals(countdownFail, evaluator.getCountdownStatistics().getFailed());
            Assertions.assertEquals(missionSuccess + 1, evaluator.getMissionStatistics().getSucceeded());
        }
    }


    private MissionHealthCheckEvaluator getRelevantEvaluator(final boolean fail) {
        return getMissionHealthCheckEvaluator(this.getClass(),
                MissionOutlineDefinition.SELF_PROPELLED_RUNNABLE + fail);
    }
}
