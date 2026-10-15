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

import java.util.Comparator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * Factory and registry for discovering managed {@link DataSourceProvider} implementations via {@link ServiceLoader}.
 *
 * @author KriolOS POS
 */
public final class DataSourceFactory {

    private static final Logger LOGGER = Logger.getLogger(DataSourceFactory.class.getName());
    private static volatile DataSource customDataSource;

    private DataSourceFactory() {
    }

    /**
     * Overrides the active DataSource programmatically (e.g. for testing).
     *
     * @param ds custom DataSource or {@code null} to reset
     */
    public static void setCustomDataSource(DataSource ds) {
        customDataSource = ds;
    }

    /**
     * Resolves the primary managed DataSource from the highest priority available provider.
     *
     * @return optional containing the DataSource if a provider is active, empty otherwise
     */
    public static Optional<DataSource> getDataSource() {
        if (customDataSource != null) {
            return Optional.of(customDataSource);
        }

        ServiceLoader<DataSourceProvider> loader = ServiceLoader.load(DataSourceProvider.class);
        return loader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(DataSourceProvider::isAvailable)
                .max(Comparator.comparingInt(DataSourceProvider::getPriority))
                .flatMap(provider -> {
                    LOGGER.log(Level.INFO, "Found managed DataSourceProvider: {0} (priority {1})",
                            new Object[]{provider.getName(), provider.getPriority()});
                    return provider.getDataSource();
                });
    }

    /**
     * Resolves a named managed DataSource from the highest priority available provider.
     *
     * @param name datasource name
     * @return optional containing the named DataSource if found, empty otherwise
     */
    public static Optional<DataSource> getDataSource(String name) {
        if (customDataSource != null) {
            return Optional.of(customDataSource);
        }

        ServiceLoader<DataSourceProvider> loader = ServiceLoader.load(DataSourceProvider.class);
        return loader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(DataSourceProvider::isAvailable)
                .max(Comparator.comparingInt(DataSourceProvider::getPriority))
                .flatMap(provider -> provider.getDataSource(name));
    }
}
