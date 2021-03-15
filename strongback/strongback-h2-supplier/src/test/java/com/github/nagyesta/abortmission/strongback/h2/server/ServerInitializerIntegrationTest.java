package com.github.nagyesta.abortmission.strongback.h2.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import com.github.nagyesta.abortmission.strongback.h2.repository.LaunchStatisticsRepository;
import org.jdbi.v3.core.JdbiException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.sql.DataSource;
import java.util.List;

import static com.github.nagyesta.abortmission.strongback.h2.server.H2DataSourceProvider.createDefaultDataSource;
import static com.github.nagyesta.abortmission.strongback.h2.server.H2DataSourceProvider.jdbi;

@Tag("integration")
@Execution(ExecutionMode.SAME_THREAD)
class ServerInitializerIntegrationTest {

    private static final int PORT = 32342;
    private static final String PASS = "pass";
    private final H2ServerManager h2ServerManager = new H2ServerManager(PASS, PORT, true);

    @BeforeEach
    void setUp() {

    }

    @AfterEach
    void tearDown() {
        h2ServerManager.stopServerQuietly();
    }

    @Test
    void testInitializeShouldCreateTheTableWhenCalledOnce() {
        try {
            //given
            h2ServerManager.startServer();
            final DataSource dataSource = createDefaultDataSource(PORT);
            final H2SchemaInitializer underTest = new H2SchemaInitializer(dataSource);
            Assertions.assertFalse(tableExists(dataSource));

            //when
            Assertions.assertDoesNotThrow(underTest::initialize);

            //then exception
            Assertions.assertTrue(tableExists(dataSource));
        } finally {
            h2ServerManager.stopServerQuietly();
        }
    }

    @Test
    void testInitializeShouldThrowExceptionWhenCalledTwice() {
        try {
            //given
            h2ServerManager.startServer();
            final DataSource dataSource = createDefaultDataSource(PORT);
            final H2SchemaInitializer underTest = new H2SchemaInitializer(dataSource);
            Assertions.assertDoesNotThrow(underTest::initialize);

            //when
            Assertions.assertThrows(StrongbackException.class, underTest::initialize);

            //then exception
        } finally {
            h2ServerManager.stopServerQuietly();
        }
    }

    private boolean tableExists(final DataSource dataSource) {
        try {
            final List<String> list = jdbi(dataSource).withExtension(LaunchStatisticsRepository.class,
                    LaunchStatisticsRepository::fetchAllMatcherNames);
            return list.isEmpty();
        } catch (final JdbiException e) {
            return false;
        }
    }
}
