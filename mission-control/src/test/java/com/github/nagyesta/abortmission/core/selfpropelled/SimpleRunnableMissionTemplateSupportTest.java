package com.github.nagyesta.abortmission.core.selfpropelled;

import com.github.nagyesta.abortmission.core.annotation.AnnotationContextEvaluator;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.opentest4j.TestAbortedException;

import java.util.Optional;
import java.util.function.Consumer;

@SuppressAbortDecisions
class SimpleRunnableMissionTemplateSupportTest extends AbstractMissionTemplateSupportTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSimpleRunnableShouldLogLifeCycleEventsWhenCalled(final boolean fail) {
        //given
        AnnotationContextEvaluator.shared().findAndApplyLaunchPlanDefinition(SimpleRunnableMissionTemplateSupport.class);

        final MissionHealthCheckEvaluator evaluator = getRelevantEvaluator();
        final int countdownStart = evaluator.getCountdownStartCount();
        final int countdownComplete = evaluator.getCountdownCompleteCount();
        final int missionFail = evaluator.getMissionFailureCount();
        final int missionSuccess = evaluator.getMissionSuccessCount();

        final SimpleRunnableMissionTemplateSupport underTest = new SimpleRunnableMissionTemplateSupport(
                MissionOutlineDefinition.SELF_PROPELLED_RUNNABLE,
                this.getClass(), () -> {
            throw new TestAbortedException();
        }) {
            @Override
            public Consumer<Optional<Void>> missionPayloadConsumer() {
                return v -> {
                    Assertions.assertEquals(countdownStart + 1, evaluator.getCountdownStartCount());
                    Assertions.assertEquals(countdownComplete + 1, evaluator.getCountdownCompleteCount());
                    Assertions.assertEquals(missionFail, evaluator.getMissionFailureCount());
                    Assertions.assertEquals(missionSuccess, evaluator.getMissionSuccessCount());
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
        Assertions.assertEquals(countdownStart + 1, evaluator.getCountdownStartCount());
        Assertions.assertEquals(countdownComplete + 1, evaluator.getCountdownCompleteCount());
        if (fail) {
            Assertions.assertEquals(missionFail + 1, evaluator.getMissionFailureCount());
            Assertions.assertEquals(missionSuccess, evaluator.getMissionSuccessCount());
        } else {
            Assertions.assertEquals(missionFail, evaluator.getMissionFailureCount());
            Assertions.assertEquals(missionSuccess + 1, evaluator.getMissionSuccessCount());
        }
    }


    private MissionHealthCheckEvaluator getRelevantEvaluator() {
        return getMissionHealthCheckEvaluator(SimpleRunnableMissionTemplateSupport.class, MissionOutlineDefinition.SELF_PROPELLED_RUNNABLE);
    }
}
