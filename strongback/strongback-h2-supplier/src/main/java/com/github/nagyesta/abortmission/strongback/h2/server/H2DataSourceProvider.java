package com.github.nagyesta.abortmission.strongback.h2.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackConfigurationHelper;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.jdbcx.JdbcDataSource;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.enums.EnumStrategy;
import org.jdbi.v3.core.enums.Enums;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.sql.DataSource;

import static com.github.nagyesta.abortmission.strongback.h2.server.H2ServerConstants.*;

/**
 * Utility class for data source creation.
 */
public final class H2DataSourceProvider {

    private H2DataSourceProvider() {
        //util class
    }

    /**
     * Creates the default data source using the default port.
     *
     * @return dataSource
     */
    public static DataSource createDefaultDataSource() {
        final int port = StrongbackConfigurationHelper.optionalPortValue()
                .orElse(DEFAULT_H2_PORT);
        return createDefaultDataSource(port);
    }

    /**
     * Creates the default data source using the provided port.
     *
     * @param portNumber The port we need to use.
     * @return dataSource
     */
    public static DataSource createDefaultDataSource(final int portNumber) {
        final JdbcDataSource ds = new JdbcDataSource();
        ds.setURL(String.format(DEFAULT_H2_JDBC_CONNECTION_URL_FORMAT, portNumber, portNumber));
        ds.setUser(DEFAULT_H2_JDBC_USER_NAME);
        ds.setPassword(DEFAULT_H2_JDBC_PASSWORD);
        return JdbcConnectionPool.create(ds);
    }

    /**
     * Creates the JDBI instance using the provided data source and configures it properly.
     *
     * @param dataSource The data source we need to use for getting a connection.
     * @return The configured JDBI instance
     */
    public static Jdbi jdbi(final DataSource dataSource) {
        final Jdbi jdbi = Jdbi.create(dataSource);
        jdbi.installPlugin(new SqlObjectPlugin());
        jdbi.getConfig(Enums.class).setEnumStrategy(EnumStrategy.BY_ORDINAL);
        return jdbi;
    }
}
