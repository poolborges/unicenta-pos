package com.openbravo.pos.spi.core;

import java.util.Objects;

/**
 * Immutable metadata record representing a cataloged plugin entry discovered within the system boundaries.
 * Modernized to align with the unified URI addressing matrix by removing redundant category headers.
 * 
 * @param id The unique fully-qualified identification signature preventing classpath layout collisions.
 * @param schemaClass The static mirror class containing configuration property definitions, or Void.class if none.
 * @param serviceContract The base structural SPI contract interface type implemented by this pluggable component.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public record PluginInfo(
    String id,
    Class<?> schemaClass,
    Class<?> serviceContract
) {
    /**
     * Compact defensive validation constructor ensuring integrity across cataloging boundaries.
     */
    public PluginInfo {
        Objects.requireNonNull(id, "Cataloged plugin unique id signature must never be null");
        Objects.requireNonNull(schemaClass, "Cataloged plugin schema metadata token class must never be null");
        Objects.requireNonNull(serviceContract, "Cataloged plugin service contract interface token must never be null");
    }
}
