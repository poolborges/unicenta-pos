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
package com.openbravo.app;

import com.openbravo.pos.spi.config.BootstrapConfig;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Adapter allowing a {@link BootstrapConfig} to be consumed as {@link AppProperties}
 * during early application bootstrap (pre-database initialization).
 *
 * @author KriolOS POS
 */
public class BootstrapAppPropertiesAdapter implements AppProperties {

    private final BootstrapConfig bootstrapConfig;
    private final File configFile;

    public BootstrapAppPropertiesAdapter(BootstrapConfig bootstrapConfig) {
        this(bootstrapConfig, (bootstrapConfig instanceof BootstrapConfiguration bc) ? bc.getConfigFile() : null);
    }

    public BootstrapAppPropertiesAdapter(BootstrapConfig bootstrapConfig, File configFile) {
        this.bootstrapConfig = bootstrapConfig;
        this.configFile = configFile;
    }

    public BootstrapConfig getBootstrapConfig() {
        return bootstrapConfig;
    }

    @Override
    public File getConfigFile() {
        return configFile;
    }

    @Override
    public String getHost() {
        return (bootstrapConfig != null) ? bootstrapConfig.getHost() : "localhost";
    }

    @Override
    public String getProperty(String sKey) {
        return (bootstrapConfig != null) ? bootstrapConfig.getProperty(sKey) : null;
    }

    @Override
    public String getProperty(String sKey, String defaultValue) {
        return (bootstrapConfig != null) ? bootstrapConfig.getProperty(sKey, defaultValue) : defaultValue;
    }

    @Override
    public List<DatabaseConfig> getAll() {
        if (bootstrapConfig == null) {
            return Collections.emptyList();
        }
        return bootstrapConfig.getDatabaseConfigs().stream()
                .map(d -> new DatabaseConfig(d.name(), d.url(), d.username(), d.password()))
                .collect(Collectors.toList());
    }

    @Override
    public DatabaseConfig getPrimary() {
        if (bootstrapConfig == null) {
            return null;
        }
        BootstrapConfig.DatabaseConfig p = bootstrapConfig.getPrimaryDatabaseConfig();
        return (p != null) ? new DatabaseConfig(p.name(), p.url(), p.username(), p.password()) : null;
    }
}
