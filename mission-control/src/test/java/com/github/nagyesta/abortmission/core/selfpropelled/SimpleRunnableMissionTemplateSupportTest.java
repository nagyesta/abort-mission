package com.github.nagyesta.abortmission.core.selfpropelled;

import com.github.nagyesta.abortmission.core.annotation.AnnotationContextEvaluator;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressAbortDecisions
class SimpleRunnableMissionTemplateSupportTest extends AbstractMissionTemplateSupportTest {


    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSimpleRunnableShouldLogLifeCycleEventsWhenCalled(final boolean fail) {
        //given
        AnnotationContextEvaluator.shared().findAndApplyLaunchPlanDefinition(SimpleRunnableMissionTemplateSupport.class);

        final MissionHealthCheckEvaluator evaluator = getRelevantEvaluator(fail);
        final int countdownStart = evaluator.getCountdownStatistics().getSnapshot().getTotal();
        final int countdownComplete = evaluator.getCountdownStatistics().getSnapshot().getSucceeded();
        final int missionFail = evaluator.getMissionStatistics().getSnapshot().getFailed();
        final int missionSuccess = evaluator.getMissionStatistics().getSnapshot().getSucceeded();

        final SimpleRunnableMissionTemplateSupport underTest = new SimpleRunnableMissionTemplateSupport(
                MissionOutlineDefinition.SELF_PROPELLED_RUNNABLE + fail,
                this.getClass(), AbstractMissionTemplateSupportTest::abort) {
            @Override
            public Consumer<Optional<Void>> missionPayloadConsumer() {
                return v -> {
                    Assertions.assertEquals(countdownStart + 1, evaluator.getCountdownStatistics().getSnapshot().getTotal());
                    Assertions.assertEquals(countdownComplete + 1, evaluator.getCountdownStatistics().getSnapshot().getSucceeded());
                    Assertions.assertEquals(missionFail, evaluator.getMissionStatistics().getSnapshot().getFailed());
                    Assertions.assertEquals(missionSuccess, evaluator.getMissionStatistics().getSnapshot().getSucceeded());

                    if (fail) {
                        throw new IllegalStateException();
                    }
                };
            }
        };

        //when
        try {
            underTest.run();
        } catch (final IllegalStateException ignore) {

        }

        //then
        Assertions.assertEquals(countdownStart + 1, evaluator.getCountdownStatistics().getSnapshot().getTotal());
        Assertions.assertEquals(countdownComplete + 1, evaluator.getCountdownStatistics().getSnapshot().getSucceeded());
        if (fail) {
            Assertions.assertEquals(missionFail + 1, evaluator.getMissionStatistics().getSnapshot().getFailed());
            Assertions.assertEquals(missionSuccess, evaluator.getMissionStatistics().getSnapshot().getSucceeded());
        } else {
            Assertions.assertEquals(missionFail, evaluator.getMissionStatistics().getSnapshot().getFailed());
            Assertions.assertEquals(missionSuccess + 1, evaluator.getMissionStatistics().getSnapshot().getSucceeded());
        }
    }

    private MissionHealthCheckEvaluator getRelevantEvaluator(final boolean fail) {
        return getMissionHealthCheckEvaluator(this.getClass(),
                MissionOutlineDefinition.SELF_PROPELLED_RUNNABLE + fail);
    }
}
