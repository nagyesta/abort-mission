package com.github.nagyesta.abortmission.strongback.base;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * Util class for strongback configuration.
 */
public final class StrongbackConfigurationHelper {

    /**
     * The property name we can use to specify the port used by the implementing strongback.
     */
    public static final String SERVER_PORT_PROPERTY_NAME = "abort-mission.telemetry.server.port";
    /**
     * The property name we need to use to indicate that we need to use an externally provided server instead of the embedded.
     */
    public static final String SERVER_USE_EXTERNAL_PROPERTY_NAME = "abort-mission.telemetry.server.useExternal";
    /**
     * The property name we need to use for obtaining the server management password for startup and shutdown.
     */
    public static final String SERVER_PASSWORD_PROPERTY_NAME = "abort-mission.telemetry.server.password";

    private static final String INT_ONLY = "^[0-9]+$";
    private static final String BOOLEAN_ONLY = "^(true|false)$";


    private StrongbackConfigurationHelper() {
        //util class
    }

    /**
     * Parses an integer property value if possible.
     *
     * @param propertyValue The string value of the property.
     * @return An optional with the parsed integer (or null if parse fails).
     */
    public static Optional<Integer> safeParseIntType(final String propertyValue) {
        return Optional.ofNullable(propertyValue)
                .filter(s -> Pattern.compile(INT_ONLY).matcher(s).matches())
                .map(Integer::parseInt);
    }

    /**
     * Parses a boolean property value if possible.
     *
     * @param propertyValue The string value of the property.
     * @return true if the value was parsed and was true, false otherwise.
     */
    public static boolean safeParseBooleanType(final String propertyValue) {
        return Optional.ofNullable(propertyValue)
                .map(String::toLowerCase)
                .filter(s -> Pattern.compile(BOOLEAN_ONLY).matcher(s).matches())
                .map(Boolean::parseBoolean)
                .orElse(false);
    }

    /**
     * Evaluates the default port configuration property and returns the parsed value as an optional.
     *
     * @return An optional with the parsed integer (or null if parse fails).
     */
    public static Optional<Integer> optionalPortValue() {
        return safeParseIntType(System.getProperty(SERVER_PORT_PROPERTY_NAME));
    }


    /**
     * Evaluates the external usage configuration property and returns the control flag value.
     *
     * @return true if the value was parsed and was true, false otherwise.
     */
    public static boolean useExternalServer() {
        return safeParseBooleanType(System.getProperty(SERVER_USE_EXTERNAL_PROPERTY_NAME));
    }

    /**
     * Evaluates the password property and returns the value wrapped into an optional.
     *
     * @return The provided password.
     */
    public static Optional<String> optionalServerPassword() {
        return Optional.ofNullable(System.getProperty(SERVER_PASSWORD_PROPERTY_NAME));
    }
}
