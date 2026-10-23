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
package com.openbravo.pos.config;

import com.openbravo.basic.BasicException;
import com.openbravo.app.config.LegacyBootstrapConfiguration;
import com.openbravo.data.loader.Session;
import com.openbravo.pos.container.BeanContainer;
import com.openbravo.pos.panels.AppView;
import com.openbravo.pos.spi.config.BootstrapConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ApplicationConfigurationService Test Suite (Issue #74)")
public class ApplicationConfigurationServiceTest {

    private Session session;
    private File tempFile;
    private LegacyBootstrapConfiguration bootstrapConfig;
    private DataLogicConfiguration dataLogic;
    private ApplicationConfigurationServiceImpl configService;

    @BeforeEach
    public void setUp() throws SQLException, IOException, BasicException {
        // Create in-memory HSQLDB session
        String dbName = "mem_config_test_" + UUID.randomUUID().toString().replace("-", "");
        session = new Session("jdbc:hsqldb:mem:" + dbName + ";sql.syntax_mys=true", "SA", "");

        // Setup test schema fixture (matches Liquibase v5_0_2 definition)
        try (var stmt = session.getConnection().createStatement()) {
            stmt.executeUpdate(
                    "CREATE TABLE config_properties ("
                    + "pos_id VARCHAR(36) NOT NULL, "
                    + "config_key VARCHAR(128) NOT NULL, "
                    + "config_value VARCHAR(4096), "
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, "
                    + "PRIMARY KEY (pos_id, config_key))"
            );
        }

        tempFile = File.createTempFile("kriolos-config-test-", ".properties");
        bootstrapConfig = new LegacyBootstrapConfiguration(tempFile);
        bootstrapConfig.setProperty("bootstrap.only.key", "bootstrap-val");
        bootstrapConfig.setProperty("overridden.key", "bootstrap-layer");
        bootstrapConfig.setProperty("company.name", "KriolOS Store Global");
        bootstrapConfig.save();

        dataLogic = new DataLogicConfiguration();
        dataLogic.init(session);

        configService = new ApplicationConfigurationServiceImpl(dataLogic, bootstrapConfig);
    }

    @AfterEach
    public void tearDown() throws SQLException {
        if (session != null) {
            session.close();
        }
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
        BeanContainer.cleanAll();
    }

    @Test
    @DisplayName("Should fall back to LegacyBootstrapConfiguration when DB has no entry")
    public void testBootstrapFallback() {
        assertEquals("bootstrap-val", configService.getProperty("bootstrap.only.key"));
        assertEquals("default-fallback", configService.getProperty("nonexistent.key", "default-fallback"));
    }

    @Test
    @DisplayName("Global property should override bootstrap fallback")
    public void testGlobalOverrideBootstrap() throws BasicException {
        configService.setGlobalProperty("overridden.key", "global-layer");

        assertEquals("global-layer", configService.getProperty("overridden.key"));
    }

    @Test
    @DisplayName("Station property should override both global and bootstrap properties")
    public void testStationOverrideGlobalAndBootstrap() throws BasicException {
        configService.setGlobalProperty("overridden.key", "global-layer");
        configService.setProperty("overridden.key", "station-specific-val");

        // Active station gets station-specific value
        assertEquals("station-specific-val", configService.getProperty("overridden.key"));

        // Another station gets global value
        String otherStationId = UUID.randomUUID().toString();
        assertEquals("global-layer", configService.getStationProperty(otherStationId, "overridden.key"));
    }

    @Test
    @DisplayName("Property mutation and removal should update cache and DB atomically")
    public void testPropertyMutationAndRemoval() throws BasicException {
        configService.setProperty("test.dyn.key", "hello-world");
        assertEquals("hello-world", configService.getProperty("test.dyn.key"));

        // Verify in DB via dataLogic
        var stationEntries = dataLogic.readByPosId(configService.getStationId());
        boolean foundInDb = stationEntries.stream()
                .anyMatch(e -> "test.dyn.key".equals(e.key()) && "hello-world".equals(e.value()));
        assertTrue(foundInDb, "Property should be persisted in database");

        // Remove property
        configService.removeProperty(configService.getStationId(), "test.dyn.key");
        assertNull(configService.getProperty("test.dyn.key"));
    }

    @Test
    @DisplayName("Bulk property retrieval should return correct maps")
    public void testBulkProperties() throws BasicException {
        configService.setGlobalProperty("global.k1", "v1");
        configService.setGlobalProperty("global.k2", "v2");
        configService.setProperty("station.k1", "sv1");

        Map<String, String> globalMap = configService.getAllGlobalProperties();
        assertEquals("v1", globalMap.get("global.k1"));
        assertEquals("v2", globalMap.get("global.k2"));
        assertNull(globalMap.get("station.k1"));

        Map<String, String> stationMap = configService.getAllActiveStationProperties();
        assertEquals("sv1", stationMap.get("station.k1"));
    }

    @Test
    @DisplayName("Should resolve ApplicationConfigurationService in BeanContainer")
    public void testBeanContainerResolution() {
   

        // Cria um mock tipado de AppView sem Proxy manual
        AppView appViewMock = Mockito.mock(AppView.class);

        // Define o comportamento com segurança de tipos (Type-safe)
        Mockito.when(appViewMock.getSession()).thenReturn(session);
        Mockito.when(appViewMock.getBootstrapConfig()).thenReturn(bootstrapConfig);

        Object bean = BeanContainer.getBean(ApplicationConfigurationService.class, appViewMock);

        assertNotNull(bean, "BeanContainer should resolve ApplicationConfigurationService");
        assertInstanceOf(ApplicationConfigurationServiceImpl.class, bean);
    }

    @Test
    @DisplayName("ApplicationProperties should delegate to ApplicationConfigurationService with fallback")
    public void testApplicationPropertiesDelegation() throws BasicException {
        configService.setGlobalProperty("company.address", "123 Main St");

        ApplicationProperties appProps = new ApplicationProperties(configService, bootstrapConfig);
        assertEquals("123 Main St", appProps.getProperty("company.address"));
        assertEquals("bootstrap-val", appProps.getProperty("bootstrap.only.key"));
        assertEquals("fallback-default", appProps.getProperty("missing.key", "fallback-default"));
        assertEquals(bootstrapConfig.getHost(), appProps.getHost());
    }
}
