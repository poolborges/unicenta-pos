package com.openbravo.pos.spi.core;

import java.util.Map;

/**
 * Interface contract used to decouple configuration persistence mechanics from runtime engine nodes.
 * Maintained strictly inside the core infrastructure package to shield third-party providers from data layers.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public interface ConfigurationStore {

    /**
     * Fetches saved configuration settings from the underlying storage layer using the plugin ID as a namespace boundary.
     * 
     * @param pluginId The fully-qualified identification signature of the pluggable module.
     * @return A Map containing the stored property values indexed precisely by their key tokens. Must never be null.
     */
    Map<String, String> loadSettings(String pluginId);
}
