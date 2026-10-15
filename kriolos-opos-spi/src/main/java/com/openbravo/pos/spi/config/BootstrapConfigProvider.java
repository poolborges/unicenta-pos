//    KriolOS POS
//    Copyright (c) 2019-2026 KriolOS
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.spi.config;

/**
 * Service Provider Interface (SPI) for providing {@link BootstrapConfig}.
 * <p>
 * Allows different runtime environments (standard desktop file, Quarkus Desktop via
 * MicroProfile Config, headless test containers) to provide pre-DB bootstrap settings
 * without coupling the core application to a specific configuration source.
 *
 * @author KriolOS POS
 */
public interface BootstrapConfigProvider {

    /**
     * Unique identifier for this provider (e.g. "file", "quarkus", "in-memory").
     *
     * @return provider name
     */
    String getName();

    /**
     * Provider priority. Higher values take precedence when multiple providers are present.
     * <p>Standard desktop file provider defaults to 0. Framework-specific providers (e.g. Quarkus)
     * should use higher values (e.g. 100).</p>
     *
     * @return priority integer
     */
    int getPriority();

    /**
     * Indicates whether this provider is currently available and active in the running JVM.
     *
     * @return {@code true} if available
     */
    boolean isAvailable();

    /**
     * Resolves the {@link BootstrapConfig} supplied by this provider.
     *
     * @return the bootstrap configuration instance
     */
    BootstrapConfig getBootstrapConfig();
}
