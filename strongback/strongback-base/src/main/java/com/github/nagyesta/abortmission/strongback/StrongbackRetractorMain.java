package com.github.nagyesta.abortmission.strongback;

import com.github.nagyesta.abortmission.strongback.base.StrongbackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * Entry point for post-test tear-down.
 */
@ComponentScan
@ImportResource(locations = "classpath:/strongback-blueprint.xml")
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class StrongbackRetractorMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrongbackRetractorMain.class);

    public static void main(final String[] args) {
        LOGGER.info("Retracting Strongback...");
        new AnnotationConfigApplicationContext(StrongbackRetractorMain.class)
                .getBean(StrongbackController.class).retract();
        LOGGER.info("Completed.");
    }

}
