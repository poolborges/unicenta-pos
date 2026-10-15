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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Bootstrap Configuration handling pre-database startup settings:
 * <ul>
 *   <li>Database connection profiles (URL, user, password, driver)</li>
 *   <li>Station identity ({@code machine.uuid})</li>
 *   <li>Configuration provider selection ({@code pos.config.provider})</li>
 *   <li>Startup LookAndFeel theme and UI language locale</li>
 *   <li>Physical file path resolution (XDG compliant)</li>
 * </ul>
 *
 * <p>Separated from application business configuration according to Architecture Specification:
 * {@code docs/modules/guide-devel/pages/spec-database-configuration.adoc}.
 * Does NOT implement AppProperties as bootstrap is strictly used during initial setup.
 */
public class BootstrapConfiguration implements BootstrapConfig {

    private static final Logger LOGGER = Logger.getLogger(BootstrapConfiguration.class.getName());

    public static final String KEY_MACHINE_UUID = "machine.uuid";
    public static final String KEY_CONFIG_PROVIDER = "pos.config.provider";
    public static final String DEFAULT_PROVIDER = "database";
    public static final String GLOBAL_POS_ID = "0000-0000-0000-0000";

    protected Properties properties;
    protected File configfile;

    private static final String APP_CONFIG_FILE_NAME = AppLocal.APP_ID + ".properties";

    public BootstrapConfiguration() {
        this(null);
    }

    public BootstrapConfiguration(File configfile) {
        if (configfile != null) {
            this.configfile = configfile;
        } else {
            this.configfile = getDefaultConfigFile();
        }
        this.properties = new SortedStoreProperties();
    }

    public static File getBaseApplicationDataDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        String baseDirectory;

        if (os.contains("win")) {
            baseDirectory = Paths.get(System.getenv("APPDATA") != null ? System.getenv("APPDATA") : userHome, "KriolOS").toString();
        } else if (os.contains("mac")) {
            baseDirectory = Paths.get(userHome, "Library", "Application Support", "KriolOS").toString();
        } else {
            String xdgData = System.getenv("XDG_DATA_HOME");
            baseDirectory = (xdgData != null && !xdgData.isEmpty())
                    ? Paths.get(xdgData, "kriolos").toString()
                    : Paths.get(userHome, ".local", "share", "kriolos").toString();
        }

        File configDir = new File(baseDirectory);
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        return configDir;
    }

    public static File getDefaultConfigFile() {
        String customPath = System.getProperty("kriolos.config");
        if (customPath == null || customPath.isBlank()) {
            customPath = System.getProperty("app.config");
        }
        if (customPath == null || customPath.isBlank()) {
            customPath = System.getProperty("config.file");
        }
        if (customPath == null || customPath.isBlank()) {
            customPath = System.getenv("KRIOLOS_CONFIG");
        }
        if (customPath == null || customPath.isBlank()) {
            customPath = System.getenv("APP_CONFIG");
        }

        if (customPath != null && !customPath.isBlank()) {
            File customFile = new File(customPath.trim());
            if (customFile.isDirectory()) {
                return new File(customFile, APP_CONFIG_FILE_NAME);
            }
            return customFile;
        }

        File baseDirectory = getBaseApplicationDataDirectory();
        return new File(baseDirectory, APP_CONFIG_FILE_NAME);
    }

    public String getAppDataDirectory() {
        return getBaseApplicationDataDirectory().getAbsolutePath();
    }

    @Override
    public String getProperty(String sKey) {
        String sysVal = System.getProperty(sKey);
        if (sysVal != null && !sysVal.isBlank()) {
            return sysVal;
        }
        return properties.getProperty(sKey);
    }

    @Override
    public String getProperty(String sKey, String defaultValue) {
        String sysVal = System.getProperty(sKey);
        if (sysVal != null && !sysVal.isBlank()) {
            return sysVal;
        }
        return properties.getProperty(sKey, defaultValue);
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperty(String sKey, String sValue) {
        if (sValue == null) {
            properties.remove(sKey);
        } else {
            properties.setProperty(sKey, sValue);
        }
    }

    public Boolean getBoolean(String sKey) {
        return Boolean.valueOf(getProperty(sKey));
    }

    public boolean getBoolean(String sKey, boolean defaultValue) {
        String val = getProperty(sKey);
        return val != null ? Boolean.parseBoolean(val) : defaultValue;
    }

    public void setBoolean(String sKey, Boolean sValue) {
        if (sValue == null) {
            properties.remove(sKey);
        } else {
            properties.setProperty(sKey, sValue.toString());
        }
    }

    /**
     * Resolves the persistent Station / Terminal UUID.
     * Auto-generates a UUID and persists to the bootstrap file if not already present.
     */
    public synchronized String getStationId() {
        String uuid = getProperty(KEY_MACHINE_UUID);
        if (uuid == null || uuid.isBlank()) {
            uuid = UUID.randomUUID().toString();
            setProperty(KEY_MACHINE_UUID, uuid);
            try {
                save();
            } catch (IOException e) {
                LOGGER.log(Level.WARNING, "Could not persist generated station machine.uuid: " + e.getMessage());
            }
        }
        return uuid.trim();
    }

    public String getConfigProvider() {
        return getProperty(KEY_CONFIG_PROVIDER, DEFAULT_PROVIDER);
    }

    public void setConfigProvider(String provider) {
        setProperty(KEY_CONFIG_PROVIDER, provider);
    }

    @Override
    public String getHost() {
        String host = getProperty("machine.hostname");
        if (host != null && !host.isBlank()) {
            return host;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException eUH) {
            return "localhost";
        }
    }

    public File getConfigFile() {
        return configfile;
    }

    public void load() {
        load(this.configfile);
    }

    public void load(File file) {
        if (file == null || !file.exists()) {
            LOGGER.log(Level.INFO, "Bootstrap configuration file does not exist: {0}", (file != null ? file.getAbsolutePath() : "null"));
            return;
        }

        try (InputStream in = new FileInputStream(file)) {
            properties = new SortedStoreProperties();
            properties.load(in);
            migrateDBProperties2026();
            LOGGER.log(Level.INFO, "Loaded bootstrap configuration: {0}", file.getAbsolutePath());
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to load bootstrap configuration file: " + file.getAbsolutePath(), e);
        }
    }

    public void save() throws IOException {
        save(this.configfile);
    }

    public void save(File file) throws IOException {
        if (file == null) {
            throw new IOException("Cannot save bootstrap configuration to null file");
        }
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        try (OutputStream out = new FileOutputStream(file)) {
            properties.store(out, "KriolOS POS Bootstrap Configuration - Generated on " + new java.util.Date());
        }
    }

    public boolean delete() {
        if (configfile != null && configfile.exists()) {
            return configfile.delete();
        }
        return false;
    }

    @Override
    public BootstrapConfig.DatabaseConfig getPrimaryDatabaseConfig() {
        if (properties == null) {
            return null;
        }

        String name = getProperty("db.name", "DB 0 (SQLite)");
        String defaultUrl = "jdbc:sqlite:" + Paths.get(getAppDataDirectory(), "kriolopos.db").toString().replace('\\', '/');
        String url = getProperty("db.URL", defaultUrl);
        String user = getProperty("db.user", "");
        String password = getProperty("db.password", "");
        String decryptPass = decryptPassword(user, password);

        return new BootstrapConfig.DatabaseConfig(name, url, user, decryptPass);
    }

    @Override
    public List<BootstrapConfig.DatabaseConfig> getDatabaseConfigs() {
        List<BootstrapConfig.DatabaseConfig> dbs = new ArrayList<>();
        if (properties == null) {
            return dbs;
        }

        BootstrapConfig.DatabaseConfig primary = getPrimaryDatabaseConfig();
        if (primary != null) {
            dbs.add(primary);
        }

        Set<String> dbIds = new java.util.LinkedHashSet<>();

        // 1. Discover modern 2026 db.{id}.name format
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("db.") && key.endsWith(".name") && !key.equals("db.name")) {
                String dbId = key.substring(3, key.length() - 5);
                dbIds.add(dbId);
            }
        }

        // 2. Discover legacy db.name.{id} format
        for (String key : properties.stringPropertyNames()) {
            if (key.startsWith("db.name.") && key.length() > 8) {
                String dbId = key.substring(8);
                dbIds.add(dbId);
            }
        }

        for (String dbId : dbIds) {
            String name = getProperty("db." + dbId + ".name");
            if (name == null) {
                name = getProperty("db.name." + dbId, "Database " + dbId);
            }

            String url = getProperty("db." + dbId + ".URL");
            if (url == null) {
                url = getProperty("db.URL." + dbId);
            }

            if (url == null || url.isBlank()) {
                continue;
            }

            String user = getProperty("db." + dbId + ".user");
            if (user == null) {
                user = getProperty("db.user." + dbId, "SA");
            }

            String password = getProperty("db." + dbId + ".password");
            if (password == null) {
                password = getProperty("db.password." + dbId, "");
            }

            String decryptPass = decryptPassword(user, password);
            dbs.add(new BootstrapConfig.DatabaseConfig(name, url, user, decryptPass));
        }

        return Collections.unmodifiableList(dbs);
    }

    /**
     * Migrates secondary database properties from the legacy format to the new 2026 format.
     */
    public void migrateDBProperties2026() {
        if (properties == null) {
            return;
        }

        List<String> keysToRemove = new ArrayList<>();
        String[] suffixes = {".name", ".URL", ".user", ".password", ".schema", ".options"};

        for (String key : properties.stringPropertyNames()) {
            if (isPrimaryKey(key)) {
                continue;
            }

            if (key.startsWith("db")) {
                for (String suffix : suffixes) {
                    if (key.endsWith(suffix)) {
                        String dbId = key.substring(2, key.length() - suffix.length());
                        if (!dbId.startsWith(".")) {
                            String value = properties.getProperty(key);
                            String newKey = "db." + dbId + suffix;
                            properties.setProperty(newKey, value);
                            keysToRemove.add(key);
                        }
                        break;
                    }
                }
            }
        }

        for (String oldKey : keysToRemove) {
            properties.remove(oldKey);
        }
    }

    private boolean isPrimaryKey(String key) {
        return key.equals("db.name") || key.equals("db.URL")
                || key.equals("db.user") || key.equals("db.password")
                || key.equals("db.schema") || key.equals("db.options");
    }

    public static String decryptPassword(String username, String passEncrypted) {
        String sDBPassword = passEncrypted;
        if (username != null && passEncrypted != null && passEncrypted.startsWith("crypt:")) {
            com.openbravo.util.AltEncrypter cypher = new com.openbravo.util.AltEncrypter("cypherkey" + username);
            sDBPassword = cypher.decrypt(passEncrypted.substring(6));
        }
        return sDBPassword;
    }

    /**
     * Preserves sorted keys when writing properties to disk for clean git diffs and predictability.
     */
    public static class SortedStoreProperties extends Properties {
        private static final long serialVersionUID = 1L;

        @Override
        public synchronized Enumeration<Object> keys() {
            return Collections.enumeration(new TreeSet<>(super.keySet()));
        }

        @Override
        public Set<Object> keySet() {
            return new TreeSet<>(super.keySet());
        }

        @Override
        public Set<Map.Entry<Object, Object>> entrySet() {
            Set<Map.Entry<Object, Object>> set = new TreeSet<>((o1, o2) ->
                    o1.getKey().toString().compareTo(o2.getKey().toString()));
            set.addAll(super.entrySet());
            return set;
        }
    }
}
