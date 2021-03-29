package com.github.nagyesta.abortmission.strongback.rmi.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.rmi.ConnectException;

@Tag("integration")
class RmiServiceProviderIntegrationTest {

    private static final int PORT_NUMBER_1 = 30033;
    private static final int PORT_NUMBER_2 = 30034;

    @Test
    void testCreateRegistryShouldThrowExceptionWhenCalledTwice() {
        //given

        //when
        Assertions.assertDoesNotThrow(() -> RmiServiceProvider.createRegistry(PORT_NUMBER_1));
        Assertions.assertThrows(StrongbackException.class, () -> RmiServiceProvider.createRegistry(PORT_NUMBER_1));

        //then + exception
    }

    @Test
    void testLookupRegistryShouldSucceedWhenCalledAfterCreatingFirst() {
        //given
        Assertions.assertThrows(ConnectException.class, () -> RmiServiceProvider.lookupRegistry(PORT_NUMBER_2).list());
        RmiServiceProvider.createRegistry(PORT_NUMBER_2);

        //when
        Assertions.assertDoesNotThrow(() -> RmiServiceProvider.lookupRegistry(PORT_NUMBER_2).list());

        //then + exception
    }
}
