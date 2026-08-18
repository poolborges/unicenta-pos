/*
 * Copyright (C) 2025 KriolOS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.pos.spi.provider;

import java.util.Objects;

/**
 * Immutable metadata record defining a single configurable parameter required by a POS extension.
 * Embedded with internationalization key capabilities to support runtime UI language translation matrices.
 * 
 * @param key The unique internal structural key identifier utilized to store and fetch the property value.
 * @param label The default user-facing descriptive text displayed adjacent to the GUI input component.
 * @param i18nLabelKey The resource bundle bundle property key identifier used to translate the label component dynamically.
 * @param description A detailed tooltip message or technical help text explaining the purpose of this config block.
 * @param type The strongly-typed data validation category used to orchestrate UI layout parsing.
 * @param required Enforces validation constraints, preventing checkout execution if this parameter remains unassigned.
 * @param defaultValue An optional baseline fallback string applied automatically during initial setup discovery phases.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public record ConfigProperty(
    String key,
    String label,
    String i18nLabelKey,
    String description,
    PropertyType type,
    boolean required,
    String defaultValue
) {
    /**
     * Compact defensive validation constructor ensuring integrity across configuration metadata boundaries.
     */
    public ConfigProperty {
        Objects.requireNonNull(key, "Property configuration key signature must never be null");
        Objects.requireNonNull(label, "Property user-facing baseline label string must never be null");
        Objects.requireNonNull(i18nLabelKey, "Property internationalization translation label key must never be null");
        Objects.requireNonNull(type, "Property validation strong data type must never be null");
    }
}
