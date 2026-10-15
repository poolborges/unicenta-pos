package com.openbravo.pos.spi.core;

/**
 * Service Provider Interface for advertising discoverable SPI contracts to PluginManager.
 * Modules defining new SPI plugin contracts can expose an implementation of this interface
 * to be automatically discovered by PluginManager via ServiceLoader, eliminating the need
 * to manually modify PluginManager.
 *
 * @author KriolOS POS
 * @since 1.0.0
 */
@FunctionalInterface
public interface PluginContractProvider {

    /**
     * Resolves the SPI contract interface class.
     *
     * @return The contract interface type (must not be null).
     */
    Class<?> getContract();
}
