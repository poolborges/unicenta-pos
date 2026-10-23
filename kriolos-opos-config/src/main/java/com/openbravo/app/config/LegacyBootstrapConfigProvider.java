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
package com.openbravo.app.config;

import com.openbravo.pos.spi.config.BootstrapConfig;
import com.openbravo.pos.spi.config.BootstrapConfigProvider;
import java.io.File;

/**
 * Standard desktop {@link BootstrapConfigProvider} implementation reading from the local filesystem
 * (kriolos-opos.properties).
 *
 * @author KriolOS POS
 */
public class LegacyBootstrapConfigProvider implements BootstrapConfigProvider {

    private final File configFile;

    public LegacyBootstrapConfigProvider() {
        this(null);
    }

    public LegacyBootstrapConfigProvider(File configFile) {
        this.configFile = configFile;
    }

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public int getPriority() {
        return 0; // Default priority for local filesystem fallback
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public BootstrapConfig getBootstrapConfig() {
        LegacyBootstrapConfiguration config = (configFile != null)
                ? new LegacyBootstrapConfiguration(configFile)
                : new LegacyBootstrapConfiguration();
        config.load();
        return config;
    }
}
