package com.github.nagyesta.abortmission.strongback.h2.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import static org.mockito.Mockito.*;

@Tag("unit")
class H2SchemaInitializerTest {

    @Test
    void testReadCreateScriptShouldThrowExceptionWhenResourceNotFound() {
        //given
        final DataSource dataSource = mock(DataSource.class);
        final H2SchemaInitializer underTest = spy(new H2SchemaInitializer(dataSource));
        doAnswer(a -> H2SchemaInitializerTest.class.getResourceAsStream("/resource-which-is-not-found"))
                .when(underTest).nullableCreateSchemaResourceAsStream();

        //when
        Assertions.assertThrows(StrongbackException.class, underTest::readCreateScript);

        //then + exception
    }
}
