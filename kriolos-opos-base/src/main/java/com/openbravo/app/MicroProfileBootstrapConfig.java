package com.openbravo.app;

import com.openbravo.pos.spi.config.BootstrapConfig;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * {@link BootstrapConfig} implementation backed by Eclipse MicroProfile Config
 * (e.g. SmallRye Config / Quarkus).
 * <p>
 * Evaluates configuration properties using standard MicroProfile precedence:
 * System Properties (400) &gt; Environment Variables (300) &gt; {@code kriolos-opos.properties} (105)
 * &gt; {@code application.properties} (100).
 *
 * @author KriolOS
 */
public class MicroProfileBootstrapConfig implements BootstrapConfig {

    private final Config config;

    public MicroProfileBootstrapConfig() {
        this(ConfigProvider.getConfig());
    }

    public MicroProfileBootstrapConfig(Config config) {
        this.config = Objects.requireNonNull(config, "config must not be null");
    }

    public Config getConfig() {
        return config;
    }

    @Override
    public String getProperty(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return config.getOptionalValue(key, String.class).orElse(null);
    }

    @Override
    public String getProperty(String key, String defaultValue) {
        String val = getProperty(key);
        return (val != null && !val.isBlank()) ? val : defaultValue;
    }

    @Override
    public boolean getBoolean(String key, boolean defaultValue) {
        return config.getOptionalValue(key, Boolean.class).orElse(defaultValue);
    }

    @Override
    public int getInt(String key, int defaultValue) {
        return config.getOptionalValue(key, Integer.class).orElse(defaultValue);
    }

    @Override
    public String getStationId() {
        return getProperty(KEY_MACHINE_UUID, GLOBAL_POS_ID);
    }

    @Override
    public String getConfigProvider() {
        return getProperty(KEY_CONFIG_PROVIDER, DEFAULT_PROVIDER);
    }

    @Override
    public String getHost() {
        String host = getProperty(KEY_MACHINE_HOSTNAME);
        if (host != null && !host.isBlank()) {
            return host.trim();
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "localhost";
        }
    }

    @Override
    public DatabaseConfig getPrimaryDatabaseConfig() {
        String url = getProperty("quarkus.datasource.jdbc.url");
        if (url == null || url.isBlank()) {
            url = getProperty("db.URL");
        }
        if (url == null || url.isBlank()) {
            return null;
        }

        String name = getProperty("db.name", "DB 0 ()");
        String user = getProperty("quarkus.datasource.username");
        if (user == null || user.isBlank()) {
            user = getProperty("db.user", "kriolopos");
        }
        String password = getProperty("quarkus.datasource.password");
        if (password == null) {
            password = getProperty("db.password", "");
        }
        String decryptedPassword = BootstrapConfiguration.decryptPassword(user, password);

        String driver = getProperty("quarkus.datasource.jdbc.driver");
        if (driver == null || driver.isBlank()) {
            driver = getProperty(KEY_DB_DRIVER);
        }

        return new DatabaseConfig(name, url, user, decryptedPassword, driver);
    }

    @Override
    public List<DatabaseConfig> getDatabaseConfigs() {
        List<DatabaseConfig> list = new ArrayList<>();
        DatabaseConfig primary = getPrimaryDatabaseConfig();
        if (primary != null) {
            list.add(primary);
        }

        Set<String> dbIds = new LinkedHashSet<>();
        for (String key : config.getPropertyNames()) {
            if (key.startsWith("db.") && key.endsWith(".name") && !key.equals("db.name")) {
                dbIds.add(key.substring(3, key.length() - 5));
            } else if (key.startsWith("db.name.") && key.length() > 8) {
                dbIds.add(key.substring(8));
            }
        }

        for (String id : dbIds) {
            String name = getProperty("db." + id + ".name", getProperty("db.name." + id, "Database " + id));
            String url = getProperty("db." + id + ".URL", getProperty("db.URL." + id));
            if (url == null || url.isBlank()) {
                continue;
            }
            String user = getProperty("db." + id + ".user", getProperty("db.user." + id, "SA"));
            String pass = getProperty("db." + id + ".password", getProperty("db.password." + id, ""));
            String decryptPass = BootstrapConfiguration.decryptPassword(user, pass);
            String driver = getProperty("db." + id + ".driver");
            list.add(new DatabaseConfig(name, url, user, decryptPass, driver));
        }

        return Collections.unmodifiableList(list);
    }
}
