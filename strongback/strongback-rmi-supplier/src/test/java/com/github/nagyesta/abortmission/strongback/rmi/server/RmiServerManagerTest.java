package com.github.nagyesta.abortmission.strongback.rmi.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.rmi.AlreadyBoundException;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@Tag("unit")
class RmiServerManagerTest {

    @Test
    void testStartServerShouldCatchAndConvertExceptionsWhenDoStartThrowsOne() throws Exception {
        //given
        final RmiServerManager underTest = spy(new RmiServerManager(1));
        doThrow(new AlreadyBoundException()).when(underTest).doStart();

        //when
        Assertions.assertThrows(StrongbackException.class, underTest::startServer);

        //then exception
    }


    @Test
    void testStartServerShouldNotThrowExceptionsWhenDoStartThrowsInterruptedException() throws Exception {
        //given
        final RmiServerManager underTest = spy(new RmiServerManager(1));
        doThrow(new InterruptedException()).when(underTest).doStart();

        //when
        underTest.startServer();

        //then exception
    }

    @Test
    void testStopServerShouldCatchAndConvertExceptionsWhenDoStopThrowsOne() throws Exception {
        //given
        final RmiServerManager underTest = spy(new RmiServerManager(1));
        doThrow(new AlreadyBoundException()).when(underTest).doStop();

        //when
        Assertions.assertThrows(StrongbackException.class, underTest::stopServer);

        //then exception
    }

    @Test
    void testStopServerQuietlyShouldCatchAllExceptionsWhenDoStopThrowsOne() throws Exception {
        //given
        final RmiServerManager underTest = spy(new RmiServerManager(1));
        doThrow(new AlreadyBoundException()).when(underTest).doStop();

        //when
        Assertions.assertDoesNotThrow(underTest::stopServerQuietly);

        //then exception
    }
}
