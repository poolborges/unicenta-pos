package com.openbravo.app.config;

import com.openbravo.pos.spi.config.BootstrapConfig;
import com.openbravo.pos.spi.config.BootstrapConfigFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests verifying {@link MicroProfileBootstrapConfig}, {@link MicroProfileConfigSource},
 * and provider discovery via {@link BootstrapConfigFactory}.
 *
 * @author KriolOS
 */
class MicroProfileBootstrapConfigTest {

    @AfterEach
    void cleanup() {
        System.clearProperty("pos.ui.theme");
        System.clearProperty("machine.uuid");
        System.clearProperty("machine.screenmode");
        System.clearProperty("quarkus.datasource.jdbc.url");
        System.clearProperty("quarkus.datasource.username");
        System.clearProperty("quarkus.datasource.password");
        System.clearProperty("db.URL");
        System.clearProperty("db.user");
        System.clearProperty("db.password");
        BootstrapConfigFactory.setCustomInstance(null);
    }

    @Test
    void testSystemPropertiesResolution() {
        System.setProperty("pos.ui.theme", "modern-dark");
        System.setProperty("machine.uuid", "pos-lane-42");
        System.setProperty("machine.screenmode", "fullscreen");

        MicroProfileBootstrapConfig config = new MicroProfileBootstrapConfig();

        assertEquals("modern-dark", config.getUiTheme());
        assertEquals("pos-lane-42", config.getStationId());
        assertEquals("fullscreen", config.getScreenMode());
        assertTrue(config.isFullscreen());
    }

    @Test
    void testQuarkusDatasourcePropertiesMapping() {
        System.setProperty("quarkus.datasource.jdbc.url", "jdbc:mariadb://localhost:3306/kriolos");
        System.setProperty("quarkus.datasource.username", "posuser");
        System.setProperty("quarkus.datasource.password", "secret123");

        MicroProfileBootstrapConfig config = new MicroProfileBootstrapConfig();
        BootstrapConfig.DatabaseConfig db = config.getPrimaryDatabaseConfig();

        assertNotNull(db);
        assertEquals("jdbc:mariadb://localhost:3306/kriolos", db.url());
        assertEquals("posuser", db.username());
        assertEquals("secret123", db.password());
    }

    @Test
    void testKriolosPropertiesConfigSource() throws IOException {
        File tempFile = File.createTempFile("kriolos-config-test", ".properties");
        tempFile.deleteOnExit();

        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("db.URL=jdbc:postgresql://db.corp:5432/pos\n");
            writer.write("machine.uuid=station-office-01\n");
        }

        MicroProfileConfigSource source = new MicroProfileConfigSource(tempFile, 105);
        assertEquals("kriolos-opos.properties", source.getName());
        assertEquals(105, source.getOrdinal());
        assertEquals("jdbc:postgresql://db.corp:5432/pos", source.getValue("db.URL"));
        assertEquals("station-office-01", source.getValue("machine.uuid"));
    }

    @Test
    void testBootstrapConfigFactoryResolvesMicroProfileProvider() {
        System.setProperty("machine.uuid", "mp-station-99");

        BootstrapConfig resolved = BootstrapConfigFactory.load();
        assertNotNull(resolved);
        assertInstanceOf(MicroProfileBootstrapConfig.class, resolved);
        assertEquals("mp-station-99", resolved.getStationId());
    }
}
