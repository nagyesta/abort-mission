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

        final var evaluator = getRelevantEvaluator(fail);
        final var countdownStart = evaluator.getCountdownStatistics().getSnapshot().getTotal();
        final var countdownComplete = evaluator.getCountdownStatistics().getSnapshot().getSucceeded();
        final var countdownFail = evaluator.getCountdownStatistics().getSnapshot().getFailed();
        final var missionSuccess = evaluator.getMissionStatistics().getSnapshot().getSucceeded();

        final RunnableMissionTemplateSupport<Boolean> underTest = new RunnableMissionTemplateSupport<>(
                MissionOutlineDefinition.SELF_PROPELLED_RUNNABLE + fail,
                this.getClass(), AbstractMissionTemplateSupportTest::abort) {
            @Override
            public Supplier<Boolean> preLaunchPreparationSupplier() {
                return () -> {
                    //no change in advance
                    Assertions.assertEquals(countdownStart, evaluator.getCountdownStatistics().getSnapshot().getTotal());
                    Assertions.assertEquals(countdownComplete, evaluator.getCountdownStatistics().getSnapshot().getSucceeded());
                    Assertions.assertEquals(countdownFail, evaluator.getMissionStatistics().getSnapshot().getFailed());
                    Assertions.assertEquals(missionSuccess, evaluator.getMissionStatistics().getSnapshot().getSucceeded());
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
            //ignore
        }

        //then
        Assertions.assertEquals(countdownStart + 1, evaluator.getCountdownStatistics().getSnapshot().getTotal());
        if (fail) {
            Assertions.assertEquals(countdownComplete, evaluator.getCountdownStatistics().getSnapshot().getSucceeded());
            Assertions.assertEquals(countdownFail + 1, evaluator.getCountdownStatistics().getSnapshot().getFailed());
            Assertions.assertEquals(missionSuccess, evaluator.getMissionStatistics().getSnapshot().getSucceeded());
        } else {
            Assertions.assertEquals(countdownComplete + 1, evaluator.getCountdownStatistics().getSnapshot().getSucceeded());
            Assertions.assertEquals(countdownFail, evaluator.getCountdownStatistics().getSnapshot().getFailed());
            Assertions.assertEquals(missionSuccess + 1, evaluator.getMissionStatistics().getSnapshot().getSucceeded());
        }
    }


    private MissionHealthCheckEvaluator getRelevantEvaluator(final boolean fail) {
        return getMissionHealthCheckEvaluator(this.getClass(),
                MissionOutlineDefinition.SELF_PROPELLED_RUNNABLE + fail);
    }
}
