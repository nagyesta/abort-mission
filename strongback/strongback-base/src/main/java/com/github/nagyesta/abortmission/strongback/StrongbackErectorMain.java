package com.github.nagyesta.abortmission.strongback;

import com.github.nagyesta.abortmission.strongback.base.StrongbackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;

/**
 * Entry point for pre-test preparation.
 */
@ComponentScan
@ImportResource(locations = "classpath:/strongback-blueprint.xml")
@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
public class StrongbackErectorMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrongbackErectorMain.class);

    public static void main(final String[] args) {
        LOGGER.info("Setting up Strongback...");
        new AnnotationConfigApplicationContext(StrongbackErectorMain.class)
                .getBean(StrongbackController.class).erect();
        LOGGER.info("Completed.");
    }
}
