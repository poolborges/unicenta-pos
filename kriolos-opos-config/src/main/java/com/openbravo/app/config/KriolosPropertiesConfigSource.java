package com.openbravo.app.config;

import org.eclipse.microprofile.config.spi.ConfigSource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MicroProfile {@link ConfigSource} providing properties loaded from {@code kriolos-opos.properties}.
 * <p>
 * Standard ordinal is 105 (higher than default application.properties at 100, but lower than
 * environment variables at 300 and system properties at 400).
 *
 * @author KriolOS
 */
public class KriolosPropertiesConfigSource implements ConfigSource {

    private static final Logger LOGGER = Logger.getLogger(KriolosPropertiesConfigSource.class.getName());

    public static final String NAME = "kriolos-opos.properties";
    public static final int DEFAULT_ORDINAL = 105;

    private final Map<String, String> properties;
    private final int ordinal;

    public KriolosPropertiesConfigSource() {
        this(BootstrapConfiguration.getDefaultConfigFile(), DEFAULT_ORDINAL);
    }

    public KriolosPropertiesConfigSource(File configFile) {
        this(configFile, DEFAULT_ORDINAL);
    }

    public KriolosPropertiesConfigSource(File configFile, int ordinal) {
        this.ordinal = ordinal;
        this.properties = new HashMap<>();

        if (configFile != null && configFile.exists()) {
            try (InputStream in = new FileInputStream(configFile)) {
                Properties props = new Properties();
                props.load(in);
                for (String key : props.stringPropertyNames()) {
                    properties.put(key, props.getProperty(key));
                }
                LOGGER.log(Level.INFO, "Loaded {0} properties from {1}",
                        new Object[]{properties.size(), configFile.getAbsolutePath()});
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Failed to load configuration file: " + configFile.getAbsolutePath(), e);
            }
        }
    }

    @Override
    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    @Override
    public Set<String> getPropertyNames() {
        return Collections.unmodifiableSet(properties.keySet());
    }

    @Override
    public int getOrdinal() {
        return ordinal;
    }

    @Override
    public String getValue(String propertyName) {
        return properties.get(propertyName);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
