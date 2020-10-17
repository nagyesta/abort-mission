package com.github.nagyesta.abortmission.booster.jupiter.annotation;

import com.github.nagyesta.abortmission.booster.jupiter.extension.AbortMissionExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@ExtendWith(AbortMissionExtension.class)
public @interface LaunchAbortArmed {

    String value() default "";
}
