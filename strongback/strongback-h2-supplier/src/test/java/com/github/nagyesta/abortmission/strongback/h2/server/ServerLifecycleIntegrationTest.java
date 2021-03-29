package com.github.nagyesta.abortmission.strongback.h2.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

@Tag("integration")
@Execution(ExecutionMode.SAME_THREAD)
class ServerLifecycleIntegrationTest {

    private static final int PORT_1 = 32343;
    private static final int PORT_2 = 32344;
    private static final int PORT_3 = 32345;
    private static final int PORT_4 = 32346;
    private static final String PASS = "password";

    @Test
    void testStartingServerShouldThrowExceptionWhenCalledTwice() {
        //given
        final H2ServerManager underTest = new H2ServerManager(PASS, PORT_1, true);
        try {

            Assertions.assertDoesNotThrow(underTest::startServer);

            //when
            Assertions.assertThrows(StrongbackException.class, underTest::startServer);

            //then exception
        } finally {
            underTest.stopServerQuietly();
        }
    }

    @Test
    void testStartingServerShouldDoNothingsWhenCalledForExternalServerMode() {
        //given
        final H2ServerManager running = new H2ServerManager(PASS, PORT_2, true);
        final H2ServerManager underTest = new H2ServerManager(PASS, PORT_2, false);
        try {
            Assertions.assertDoesNotThrow(running::startServer);

            //when
            Assertions.assertDoesNotThrow(underTest::startServer);

            //then no exception, server still running
            Assertions.assertDoesNotThrow(running::stopServer);
        } finally {
            running.stopServerQuietly();
        }
    }

    @Test
    void testStoppingServerShouldThrowExceptionWhenCalledWhileStopped() {
        //given
        final H2ServerManager underTest = new H2ServerManager(PASS, PORT_3, true);

        //when
        Assertions.assertThrows(StrongbackException.class, underTest::stopServer);

        //then exception
    }

    @Test
    void testStoppingServerShouldDoNothingsWhenCalledForExternalServerMode() {
        //given
        final H2ServerManager running = new H2ServerManager(PASS, PORT_4, true);
        final H2ServerManager underTest = new H2ServerManager(PASS, PORT_4, false);
        try {
            Assertions.assertDoesNotThrow(running::startServer);

            //when
            Assertions.assertDoesNotThrow(underTest::stopServer);

            //then no exception, server still running
            Assertions.assertDoesNotThrow(running::stopServer);
        } finally {
            running.stopServerQuietly();
        }
    }

}
