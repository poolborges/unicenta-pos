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
package com.openbravo.pos.spi.datasource;

import java.util.Optional;
import javax.sql.DataSource;

/**
 * Service Provider Interface (SPI) providing managed {@link DataSource} instances.
 * <p>
 * Enables framework runtimes such as Quarkus Desktop to supply managed connection pools
 * (e.g. Quarkus Agroal) directly to the POS session without requiring manual JDBC connection
 * creation via raw driver strings.
 *
 * @author KriolOS POS
 */
public interface DataSourceProvider {

    /**
     * Unique identifier for this provider (e.g. "quarkus-agroal", "hikari", "default").
     *
     * @return provider name
     */
    String getName();

    /**
     * Provider priority. Higher values take precedence when multiple providers are present.
     * <p>Quarkus Agroal or container-managed pools should use high priorities (e.g. 100).</p>
     *
     * @return priority integer
     */
    int getPriority();

    /**
     * Indicates whether this provider is currently available and has an active managed DataSource.
     *
     * @return {@code true} if available
     */
    boolean isAvailable();

    /**
     * Resolves the primary / default {@link DataSource}.
     *
     * @return optional containing the DataSource if available
     */
    Optional<DataSource> getDataSource();

    /**
     * Resolves a named {@link DataSource} (e.g. for multi-tenant or secondary databases).
     *
     * @param name datasource name
     * @return optional containing the named DataSource if available
     */
    Optional<DataSource> getDataSource(String name);
}
