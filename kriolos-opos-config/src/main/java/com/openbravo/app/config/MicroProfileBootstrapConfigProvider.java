package com.openbravo.app.config;

import com.openbravo.pos.spi.config.BootstrapConfig;
import com.openbravo.pos.spi.config.BootstrapConfigProvider;
import org.eclipse.microprofile.config.ConfigProvider;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Primary {@link BootstrapConfigProvider} backing bootstrap configuration via Eclipse MicroProfile Config.
 * <p>
 * Priority 100 ensures it takes precedence over the legacy raw file provider (priority 0).
 *
 * @author KriolOS
 */
public class MicroProfileBootstrapConfigProvider implements BootstrapConfigProvider {

    private static final Logger LOGGER = Logger.getLogger(MicroProfileBootstrapConfigProvider.class.getName());

    @Override
    public String getName() {
        return "microprofile";
    }

    @Override
    public int getPriority() {
        return 100;
    }

    @Override
    public boolean isAvailable() {
        try {
            return ConfigProvider.getConfig() != null;
        } catch (Throwable t) {
            LOGGER.log(Level.FINE, "MicroProfile ConfigProvider not available: " + t.getMessage());
            return false;
        }
    }

    @Override
    public BootstrapConfig getBootstrapConfig() {
        return new MicroProfileBootstrapConfig();
    }
}
