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

/**
 * Structural contract extended by any domain manager acting as an automated factory broker.
 * Unifies discovery lookup routing, allowing client applications to request fully configured 
 * system extensions using decoupled search criteria parameters.
 * 
 * @param <K> The target lookup criteria evaluation selector key type (e.g., java.util.Locale).
 * @param <V> The base structural SPI contract interface type produced by the factory (e.g., LocalizationProvider).
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public interface ProviderFactory<K, V> {

    /**
     * Resolves, instantiates, and configures the appropriate service provider matching the criteria.
     * 
     * @param criteria The lookup parameter key utilized to evaluate target module support thresholds.
     * @return A fully hydrated and ready-to-use implementation instance of the SPI contract. Must never be null.
     */
    V getProvider(K criteria);
}

