package com.openbravo.pos.spi.provider;

import java.util.Map;

/**
 * Structural contract extended by any pluggable service provider requiring runtime parameters.
 * Handles state injection boundaries and active execution queries for active modules.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public interface ConfigurableProvider {

    /**
     * Injects the configured values selected by the operator directly into the pluggable driver lifecycle.
     * Triggered by the core lifecycle container immediately after parameter modifications are persisted.
     * 
     * @param configurations A Map containing the user-defined values indexed precisely by their respective property key tokens.
     */
    void configure(Map<String, String> configurations);

    /**
     * Extracts an unmodifiable snapshot copy representing the active operational settings running inside this instance.
     * Useful for UI re-population routines, system health checking, or dynamic configuration auditing logs.
     * 
     * @return A Map holding the current execution property values indexed precisely by their key tokens. Must never be null.
     */
    Map<String, String> getCurrentConfiguration();
}
