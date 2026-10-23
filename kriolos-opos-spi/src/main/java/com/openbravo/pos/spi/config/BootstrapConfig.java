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

import java.util.List;

/**
 * Bootstrap Configuration contract handling initial application startup (pre-database) settings:
 * <ul>
 *   <li>Database connection profiles (URL, username, password, driver, pool settings)</li>
 *   <li>Station identity ({@code machine.uuid})</li>
 *   <li>Configuration provider selection ({@code pos.config.provider})</li>
 *   <li>Startup LookAndFeel theme</li>
 *   <li>Screen mode (window, maximized, fullscreen, kiosk)</li>
 *   <li>Single-instance / RMI configuration</li>
 *   <li>Host machine identification</li>
 * </ul>
 *
 * <p>Bootstrap is strictly separated from application runtime business configuration (Issue #74).
 * Implementations may be backed by local properties files, system/environment properties,
 * or external frameworks such as Quarkus (MicroProfile Config).</p>
 *
 * @author KriolOS POS
 */
public interface BootstrapConfig {

    // --- Station & Provider Keys ---
    String KEY_MACHINE_UUID = "machine.uuid";
    String KEY_CONFIG_PROVIDER = "pos.config.provider";
    String DEFAULT_PROVIDER = "database";
    String GLOBAL_POS_ID = "0000-0000-0000-0000";

    // --- Host & Display Keys ---
    String KEY_MACHINE_HOSTNAME = "machine.hostname";
    String KEY_MACHINE_SCREENMODE = "machine.screenmode";
    String KEY_MACHINE_UNIQUEINSTANCE = "machine.uniqueinstance";

    // --- Single-Instance RMI Keys ---
    String KEY_MACHINE_RMI_ID = "machine.rmi.id";
    String KEY_MACHINE_RMI_PORT = "machine.rmi.port";
    String DEFAULT_RMI_ID = "com.openbravo.pos.instance.Kriolos-POS";
    int DEFAULT_RMI_PORT = 3005;

    // --- UI Look & Feel Theme Key ---
    String KEY_UI_THEME = "pos.ui.theme";

    // --- Application Mode Keys ---
    String KEY_APP_DEBUG = "app.debug";

    // --- Database Pool & Engine Keys ---
    String KEY_DB_POOL_MIN_SIZE = "db.pool.min";
    String KEY_DB_POOL_MAX_SIZE = "db.pool.max";
    String KEY_DB_ENGINE = "db.engine";
    String KEY_DB_DRIVER = "db.driver";

    /**
     * Reads a configuration property by key.
     *
     * @param key property key
     * @return property value or {@code null} if not defined
     */
    String getProperty(String key);

    /**
     * Reads a configuration property by key with a fallback default value.
     *
     * @param key property key
     * @param defaultValue default value if key is not found
     * @return property value or {@code defaultValue}
     */
    String getProperty(String key, String defaultValue);

    /**
     * Reads a boolean property by key with a fallback default value.
     *
     * @param key property key
     * @param defaultValue default boolean value if key is not found
     * @return boolean property value
     */
    default boolean getBoolean(String key, boolean defaultValue){
        String val = getProperty(key);
        if (val == null || val.isBlank()) {
            return defaultValue;
        }
        try {
            return Boolean.parseBoolean(val.trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Reads an integer property by key with a fallback default value.
     *
     * @param key property key
     * @param defaultValue default integer value if key is not found or not parsable
     * @return integer property value
     */
    default int getInt(String key, int defaultValue) {
        String val = getProperty(key);
        if (val == null || val.isBlank()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Resolves the persistent Station / Terminal UUID.
     *
     * @return station UUID string
     */
    default String getStationId() {
        return getProperty(KEY_MACHINE_UUID, GLOBAL_POS_ID).trim();
    }

    /**
     * Resolves the post-DB configuration provider name (e.g. "database", "file").
     *
     * @return provider name
     */
    default String getConfigProvider() {
        return getProperty(KEY_CONFIG_PROVIDER, DEFAULT_PROVIDER);
    }

    /**
     * Resolves the local machine host name.
     *
     * @return host name
     */
    String getHost();

    /**
     * Returns all configured bootstrap database connection profiles.
     *
     * @return list of database configurations
     */
    List<DatabaseConfig> getDatabaseConfigs();

    /**
     * Returns the primary / default database connection profile.
     *
     * @return primary database configuration or {@code null}
     */
    DatabaseConfig getPrimaryDatabaseConfig();

    /**
     * Returns the configured UI Look & Feel Theme identifier or name.
     *
     * @return UI theme ID or {@code null} if not set
     */
    default String getUiTheme() {
        return getUiTheme(null);
    }

    /**
     * Returns the configured UI Look & Feel Theme identifier with a default fallback.
     *
     * @param defaultTheme fallback theme
     * @return UI theme ID or {@code defaultTheme}
     */
    default String getUiTheme(String defaultTheme) {
        String theme = getProperty(KEY_UI_THEME);
        if (theme == null || theme.isBlank()) {
            theme = getProperty("pos.ui.theme.id");
        }
        if (theme == null || theme.isBlank()) {
            theme = getProperty("swing.defaultlaf");
        }
        return (theme != null && !theme.isBlank()) ? theme.trim() : defaultTheme;
    }

    /**
     * Returns the window display mode ("window", "windowmaximised", "fullscreen", "kiosk").
     *
     * @return screen mode string
     */
    default String getScreenMode() {
        return getProperty(KEY_MACHINE_SCREENMODE, "window");
    }

    /**
     * Indicates whether the application should launch in fullscreen mode.
     *
     * @return true if fullscreen mode is requested
     */
    default boolean isFullscreen() {
        String mode = getScreenMode();
        return "fullscreen".equalsIgnoreCase(mode);
    }

    /**
     * Indicates whether the application should launch in kiosk / locked mode.
     *
     * @return true if kiosk or fullscreen
     */
    default boolean isKioskMode() {
        String mode = getScreenMode();
        return "kiosk".equalsIgnoreCase(mode) || "fullscreen".equalsIgnoreCase(mode);
    }

    /**
     * Indicates whether the window should be maximized on startup.
     *
     * @return true if maximized
     */
    default boolean isWindowMaximized() {
        return "windowmaximised".equalsIgnoreCase(getScreenMode());
    }

    /**
     * Indicates whether only a single application instance is allowed per machine.
     *
     * @return true if single instance enforcement is enabled
     */
    default boolean isUniqueInstance() {
        return getBoolean(KEY_MACHINE_UNIQUEINSTANCE, true);
    }

    /**
     * Returns the RMI instance identifier used for single-instance enforcement.
     *
     * @return RMI application ID
     */
    default String getRmiApplicationId() {
        return getProperty(KEY_MACHINE_RMI_ID, DEFAULT_RMI_ID);
    }

    /**
     * Returns the RMI port used for single-instance enforcement.
     *
     * @return RMI port
     */
    default int getRmiPort() {
        return getInt(KEY_MACHINE_RMI_PORT, DEFAULT_RMI_PORT);
    }

    /**
     * Indicates whether application debug mode is enabled.
     *
     * @return true if debug mode is active
     */
    default boolean isDebugMode() {
        return getBoolean(KEY_APP_DEBUG, false);
    }

    /**
     * Returns the minimum connection pool size for the database.
     *
     * @param defaultMin fallback minimum pool size
     * @return minimum pool size
     */
    default int getDatabasePoolMinSize(int defaultMin) {
        return getInt(KEY_DB_POOL_MIN_SIZE, defaultMin);
    }

    /**
     * Returns the maximum connection pool size for the database.
     *
     * @param defaultMax fallback maximum pool size
     * @return maximum pool size
     */
    default int getDatabasePoolMaxSize(int defaultMax) {
        return getInt(KEY_DB_POOL_MAX_SIZE, defaultMax);
    }

    /**
     * Returns the database engine name if explicitly set (e.g. "hsqldb", "sqlite", "mariadb", "mysql", "postgresql").
     *
     * @return database engine or null
     */
    default String getDatabaseEngine() {
        return getProperty(KEY_DB_ENGINE);
    }

    /**
     * Returns the database JDBC driver class name.
     *
     * @return driver class name or null
     */
    default String getDatabaseDriver() {
        return getProperty(KEY_DB_DRIVER);
    }

    /**
     * Immutable record representing a database connection profile for bootstrap.
     *
     * @param name display name
     * @param url JDBC URL
     * @param username database user
     * @param password database password
     * @param driverClass optional JDBC driver class name
     */
    record DatabaseConfig(String name, String url, String username, String password, String driverClass) {
        public DatabaseConfig(String name, String url, String username, String password) {
            this(name, url, username, password, null);
        }
    }
}
