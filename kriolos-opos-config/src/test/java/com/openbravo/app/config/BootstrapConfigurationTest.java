package com.openbravo.app.config;

import com.openbravo.pos.spi.config.BootstrapConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class BootstrapConfigurationTest {

    private File tempFile;

    @BeforeEach
    public void setUp() throws IOException {
        tempFile = File.createTempFile("kriolos-bootstrap-test-", ".properties");
    }

    @AfterEach
    public void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    public void testBootstrapCreationAndDefaults() {
        LegacyBootstrapConfiguration config = new LegacyBootstrapConfiguration(tempFile);
        assertEquals(tempFile, config.getConfigFile());
        assertEquals(LegacyBootstrapConfiguration.DEFAULT_PROVIDER, config.getConfigProvider());
        assertNotNull(config.getHost());
    }

    @Test
    public void testStationIdAutoGenerationAndPersistence() throws IOException {
        LegacyBootstrapConfiguration config = new LegacyBootstrapConfiguration(tempFile);
        String stationId = config.getStationId();
        assertNotNull(stationId);
        assertFalse(stationId.isBlank());

        // Load into another instance from the same file to verify persistence
        LegacyBootstrapConfiguration config2 = new LegacyBootstrapConfiguration(tempFile);
        config2.load();
        assertEquals(stationId, config2.getStationId());
    }

    @Test
    public void testPropertiesAccessAndSave() throws IOException {
        LegacyBootstrapConfiguration config = new LegacyBootstrapConfiguration(tempFile);
        config.setProperty("test.key", "hello-world");
        config.setBoolean("test.boolean", true);
        config.setConfigProvider("database");
        config.save();

        LegacyBootstrapConfiguration reloaded = new LegacyBootstrapConfiguration(tempFile);
        reloaded.load();

        assertEquals("hello-world", reloaded.getProperty("test.key"));
        assertTrue(reloaded.getBoolean("test.boolean"));
        assertEquals("database", reloaded.getConfigProvider());
    }

    @Test
    public void testDatabaseConfigResolution() throws IOException {
        LegacyBootstrapConfiguration config = new LegacyBootstrapConfiguration(tempFile);
        config.setProperty("db.name", "ProductionDB");
        config.setProperty("db.URL", "jdbc:mariadb://localhost:3306/pos");
        config.setProperty("db.user", "posuser");
        config.setProperty("db.password", "secret");

        config.setProperty("db.name.1", "BackupDB");
        config.setProperty("db.URL.1", "jdbc:mariadb://backup:3306/pos");
        config.setProperty("db.user.1", "posbackup");
        config.setProperty("db.password.1", "secret2");
        config.save();

        LegacyBootstrapConfiguration reloaded = new LegacyBootstrapConfiguration(tempFile);
        reloaded.load();

        BootstrapConfig.DatabaseConfig primary = reloaded.getPrimaryDatabaseConfig();
        assertEquals("ProductionDB", primary.name());
        assertEquals("jdbc:mariadb://localhost:3306/pos", primary.url());
        assertEquals("posuser", primary.username());

        List<BootstrapConfig.DatabaseConfig> all = reloaded.getDatabaseConfigs();
        assertEquals(2, all.size());
        assertEquals("BackupDB", all.get(1).name());
    }

    @Test
    public void testTypedBootstrapAccessors() throws IOException {
        LegacyBootstrapConfiguration config = new LegacyBootstrapConfiguration(tempFile);
        config.setProperty(BootstrapConfig.KEY_UI_THEME, "modern-dark");
        config.setProperty(BootstrapConfig.KEY_MACHINE_SCREENMODE, "fullscreen");
        config.setProperty(BootstrapConfig.KEY_MACHINE_RMI_PORT, "3005");
        config.setProperty(BootstrapConfig.KEY_APP_DEBUG, "true");
        config.setProperty(BootstrapConfig.KEY_DB_POOL_MIN_SIZE, "3");
        config.setProperty(BootstrapConfig.KEY_DB_POOL_MAX_SIZE, "20");
        config.save();

        LegacyBootstrapConfiguration reloaded = new LegacyBootstrapConfiguration(tempFile);
        reloaded.load();

        assertEquals("modern-dark", reloaded.getUiTheme());
        assertEquals("fullscreen", reloaded.getScreenMode());
        assertTrue(reloaded.isFullscreen());
        assertTrue(reloaded.isKioskMode());
        assertFalse(reloaded.isWindowMaximized());
        assertTrue(reloaded.isUniqueInstance());
        assertEquals(3005, reloaded.getRmiPort());
        assertTrue(reloaded.isDebugMode());
        assertEquals(3, reloaded.getDatabasePoolMinSize(1));
        assertEquals(20, reloaded.getDatabasePoolMaxSize(5));
    }
}

