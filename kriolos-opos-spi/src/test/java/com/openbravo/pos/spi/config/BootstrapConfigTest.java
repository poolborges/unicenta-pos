package com.openbravo.pos.spi.config;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class BootstrapConfigTest {

    private static class TestBootstrapConfig implements BootstrapConfig {
        private final Map<String, String> props = new HashMap<>();

        void set(String k, String v) {
            props.put(k, v);
        }

        @Override
        public String getProperty(String key) {
            return props.get(key);
        }

        @Override
        public String getProperty(String key, String defaultValue) {
            return props.getOrDefault(key, defaultValue);
        }

        @Override
        public boolean getBoolean(String key, boolean defaultValue) {
            String val = props.get(key);
            return val != null ? Boolean.parseBoolean(val) : defaultValue;
        }

        @Override
        public String getStationId() {
            return props.getOrDefault(KEY_MACHINE_UUID, "test-station-uuid");
        }

        @Override
        public String getConfigProvider() {
            return props.getOrDefault(KEY_CONFIG_PROVIDER, DEFAULT_PROVIDER);
        }

        @Override
        public String getHost() {
            return props.getOrDefault(KEY_MACHINE_HOSTNAME, "test-host");
        }

        @Override
        public List<DatabaseConfig> getDatabaseConfigs() {
            return List.of(getPrimaryDatabaseConfig());
        }

        @Override
        public DatabaseConfig getPrimaryDatabaseConfig() {
            return new DatabaseConfig("Primary", "jdbc:hsqldb:mem:test", "sa", "");
        }
    }

    @Test
    void testDefaultValues() {
        TestBootstrapConfig cfg = new TestBootstrapConfig();

        assertEquals("test-station-uuid", cfg.getStationId());
        assertEquals("database", cfg.getConfigProvider());
        assertEquals("test-host", cfg.getHost());
        assertNull(cfg.getUiTheme());
        assertEquals("darcula", cfg.getUiTheme("darcula"));
        assertEquals("window", cfg.getScreenMode());
        assertFalse(cfg.isFullscreen());
        assertFalse(cfg.isKioskMode());
        assertFalse(cfg.isWindowMaximized());
        assertTrue(cfg.isUniqueInstance());
        assertEquals(BootstrapConfig.DEFAULT_RMI_ID, cfg.getRmiApplicationId());
        assertEquals(BootstrapConfig.DEFAULT_RMI_PORT, cfg.getRmiPort());
        assertFalse(cfg.isDebugMode());
        assertEquals(2, cfg.getDatabasePoolMinSize(2));
        assertEquals(10, cfg.getDatabasePoolMaxSize(10));
        assertNull(cfg.getDatabaseEngine());
        assertNull(cfg.getDatabaseDriver());
    }

    @Test
    void testConfiguredValues() {
        TestBootstrapConfig cfg = new TestBootstrapConfig();
        cfg.set(BootstrapConfig.KEY_UI_THEME, "modern-dark");
        cfg.set(BootstrapConfig.KEY_MACHINE_SCREENMODE, "fullscreen");
        cfg.set(BootstrapConfig.KEY_MACHINE_UNIQUEINSTANCE, "false");
        cfg.set(BootstrapConfig.KEY_MACHINE_RMI_PORT, "4005");
        cfg.set(BootstrapConfig.KEY_MACHINE_RMI_ID, "com.custom.app");
        cfg.set(BootstrapConfig.KEY_APP_DEBUG, "true");
        cfg.set(BootstrapConfig.KEY_DB_POOL_MIN_SIZE, "5");
        cfg.set(BootstrapConfig.KEY_DB_POOL_MAX_SIZE, "30");
        cfg.set(BootstrapConfig.KEY_DB_ENGINE, "mariadb");
        cfg.set(BootstrapConfig.KEY_DB_DRIVER, "org.mariadb.jdbc.Driver");

        assertEquals("modern-dark", cfg.getUiTheme());
        assertEquals("fullscreen", cfg.getScreenMode());
        assertTrue(cfg.isFullscreen());
        assertTrue(cfg.isKioskMode());
        assertFalse(cfg.isWindowMaximized());
        assertFalse(cfg.isUniqueInstance());
        assertEquals(4005, cfg.getRmiPort());
        assertEquals("com.custom.app", cfg.getRmiApplicationId());
        assertTrue(cfg.isDebugMode());
        assertEquals(5, cfg.getDatabasePoolMinSize(1));
        assertEquals(30, cfg.getDatabasePoolMaxSize(10));
        assertEquals("mariadb", cfg.getDatabaseEngine());
        assertEquals("org.mariadb.jdbc.Driver", cfg.getDatabaseDriver());
    }

    @Test
    void testThemeConfiguration() {
        TestBootstrapConfig cfg = new TestBootstrapConfig();
        cfg.set(BootstrapConfig.KEY_UI_THEME, "flatlaf-dark");
        assertEquals("flatlaf-dark", cfg.getUiTheme());
    }
}
