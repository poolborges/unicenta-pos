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

import java.util.Comparator;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Factory and registry for discovering the active {@link BootstrapConfigProvider} via {@link ServiceLoader}.
 *
 * @author KriolOS POS
 */
public final class BootstrapConfigFactory {

    private static final Logger LOGGER = Logger.getLogger(BootstrapConfigFactory.class.getName());
    private static volatile BootstrapConfig customInstance;

    private BootstrapConfigFactory() {
    }

    /**
     * Overrides the active bootstrap configuration instance programmatically (e.g. for testing).
     *
     * @param instance custom bootstrap config or {@code null} to reset
     */
    public static void setCustomInstance(BootstrapConfig instance) {
        customInstance = instance;
    }

    /**
     * Resolves the active {@link BootstrapConfig} using the highest priority available {@link BootstrapConfigProvider}.
     *
     * @return the resolved {@link BootstrapConfig}
     * @throws IllegalStateException if no available provider is found on classpath
     */
    public static BootstrapConfig load() {
        if (customInstance != null) {
            return customInstance;
        }

        ServiceLoader<BootstrapConfigProvider> loader = ServiceLoader.load(BootstrapConfigProvider.class);
        Optional<BootstrapConfigProvider> selectedProvider = loader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(BootstrapConfigProvider::isAvailable)
                .max(Comparator.comparingInt(BootstrapConfigProvider::getPriority));

        if (selectedProvider.isPresent()) {
            BootstrapConfigProvider provider = selectedProvider.get();
            LOGGER.log(Level.INFO, "Resolved BootstrapConfigProvider: {0} (priority {1})",
                    new Object[]{provider.getName(), provider.getPriority()});
            return provider.getBootstrapConfig();
        }

        throw new IllegalStateException("No available BootstrapConfigProvider found on classpath");
    }
}
