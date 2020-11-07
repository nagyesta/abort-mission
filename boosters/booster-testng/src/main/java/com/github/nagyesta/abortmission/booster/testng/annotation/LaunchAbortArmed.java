package com.github.nagyesta.abortmission.booster.testng.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface LaunchAbortArmed {

    String value() default "";
}
