package com.github.nagyesta.abortmission.strongback.h2.server;

import com.github.nagyesta.abortmission.strongback.base.StrongbackException;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Component taking care of DB schema creation.
 */
public class H2SchemaInitializer {

    private static final String CREATE_TABLE_SQL = "/create_table.sql";

    private final Jdbi jdbi;

    /**
     * Creates an instance and sets the data source.
     *
     * @param dataSource The data source we need to use for the test.
     */
    public H2SchemaInitializer(final DataSource dataSource) {
        this.jdbi = H2DataSourceProvider.jdbi(dataSource);
    }

    /**
     * Executes the schema initialization script.
     */
    public void initialize() {
        try {
            this.jdbi.open().createScript(readCreateScript()).execute();
        } catch (final JdbiException ex) {
            throw new StrongbackException("Couldn't create schema.", ex);
        }
    }

    /**
     * Returns the content of the create schema script.
     *
     * @return create script
     */
    @SuppressWarnings("LocalCanBeFinal")
    protected String readCreateScript() {
        try (InputStream stream = createSchemaResourceAsStream();
             InputStreamReader reader = new InputStreamReader(stream, StandardCharsets.UTF_8);
             BufferedReader bufferedReader = new BufferedReader(reader)) {
            return bufferedReader.lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (final Exception e) {
            throw new StrongbackException("Create script cannot be loaded.", e);
        }
    }

    /**
     * Returns the stream of the create schema script.
     *
     * @return create script
     * @throws StrongbackException when the script cannot be found.
     */
    protected InputStream createSchemaResourceAsStream() throws StrongbackException {
        return Optional.ofNullable(nullableCreateSchemaResourceAsStream())
                .orElseThrow(() -> new StrongbackException("Resource was not found."));
    }

    /**
     * Returns the stream of the create schema script.
     *
     * @return create script (or null if it cannot be read).
     */
    protected InputStream nullableCreateSchemaResourceAsStream() {
        return H2SchemaInitializer.class.getResourceAsStream(CREATE_TABLE_SQL);
    }
}
