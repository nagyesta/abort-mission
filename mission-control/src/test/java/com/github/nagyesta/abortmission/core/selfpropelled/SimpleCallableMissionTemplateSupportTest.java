package com.github.nagyesta.abortmission.core.selfpropelled;

import com.github.nagyesta.abortmission.core.annotation.AnnotationContextEvaluator;
import com.github.nagyesta.abortmission.core.annotation.SuppressAbortDecisions;
import com.github.nagyesta.abortmission.core.healthcheck.MissionHealthCheckEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.function.Function;

@SuppressAbortDecisions
class SimpleCallableMissionTemplateSupportTest extends AbstractMissionTemplateSupportTest {

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void testSimpleCallableShouldLogLifeCycleEventsWhenCalled(final boolean fail) {
        //given
        AnnotationContextEvaluator.shared().findAndApplyLaunchPlanDefinition(SimpleCallableMissionTemplateSupportTest.class);

        final var evaluator = getRelevantEvaluator(fail);
        final var countdownStart = evaluator.getCountdownStatistics().getSnapshot().getTotal();
        final var countdownComplete = evaluator.getCountdownStatistics().getSnapshot().getSucceeded();
        final var missionFail = evaluator.getMissionStatistics().getSnapshot().getFailed();
        final var missionSuccess = evaluator.getMissionStatistics().getSnapshot().getSucceeded();

        final SimpleCallableMissionTemplateSupport<Boolean> underTest = new SimpleCallableMissionTemplateSupport<>(
                MissionOutlineDefinition.SELF_PROPELLED_CALLABLE + fail,
                this.getClass(), AbstractMissionTemplateSupportTest::abort) {
            @Override
            public Function<Optional<Void>, Boolean> missionPayloadFunction() {
                return v -> {
                    Assertions.assertEquals(countdownStart + 1, evaluator.getCountdownStatistics().getSnapshot().getTotal());
                    Assertions.assertEquals(countdownComplete + 1, evaluator.getCountdownStatistics().getSnapshot().getSucceeded());
                    Assertions.assertEquals(missionFail, evaluator.getMissionStatistics().getSnapshot().getFailed());
                    Assertions.assertEquals(missionSuccess, evaluator.getMissionStatistics().getSnapshot().getSucceeded());
                    if (fail) {
                        throw new IllegalStateException();
                    }
                    return false;
                };
            }
        };

        //when
        try {
            underTest.call();
        } catch (final IllegalStateException ignore) {
            //ignore
        } catch (final Exception exception) {
            Assertions.fail(exception.getMessage());
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
                MissionOutlineDefinition.SELF_PROPELLED_CALLABLE + fail);
    }
}
