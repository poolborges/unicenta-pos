package com.openbravo.pos.spi.core;

/**
 * Shared infrastructural category tokens used exclusively by the core routing engines.
 * Centralizes subsystem domain routing identifiers to ensure consistent filtering
 * across all active manager nodes within the application lifecycle.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public final class PluginCategory {

    /** Internal routing token for the Localization and regional compliance framework */
    public static final String L10N = "L10N";

    /** Internal routing token for Thermal and Matrix printing systems */
    public static final String PRINTER = "PRINTER";

    /** Internal routing token for Electronic weighing hardware units */
    public static final String SCALE = "SCALE";

    /** Internal routing token for Customer pole display modules */
    public static final String DISPLAY = "DISPLAY";

    /** Internal routing token for Cash drawers triggering routines */
    public static final String CASH_DRAWER = "CASH_DRAWER";

    /**
     * Private constructor to enforce utility class non-instantiability.
     */
    private PluginCategory() {
        throw new UnsupportedOperationException("Infrastructural constant matrix cannot be instantiated");
    }
}
