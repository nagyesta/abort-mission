package com.github.nagyesta.abortmission.strongback.h2.server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static com.github.nagyesta.abortmission.strongback.h2.server.H2DataSourceProvider.createDefaultDataSource;
import static com.github.nagyesta.abortmission.strongback.h2.server.H2ServerConstants.DEFAULT_H2_JDBC_USER_NAME;

@Tag("integration")
@Execution(ExecutionMode.SAME_THREAD)
class DataSourceIntegrationTest {

    private final H2ServerManager h2ServerManager = new H2ServerManager();

    @Test
    @SuppressWarnings("LocalCanBeFinal")
    void testCreateDefaultDataSourceShouldConnectToTheTcpServerWhenCalled() {
        try {
            //given
            h2ServerManager.startServer();

            //when
            final DataSource dataSource = createDefaultDataSource();
            try (Connection connection = dataSource.getConnection()) {

                //then no exception
                final DatabaseMetaData metaData = connection.getMetaData();
                Assertions.assertEquals(DEFAULT_H2_JDBC_USER_NAME.toUpperCase(), metaData.getUserName().toUpperCase());
            } catch (SQLException ex) {
                Assertions.fail("Connection should have succeeded.", ex);
            }
        } finally {
            h2ServerManager.stopServerQuietly();
        }
    }
}
