package com.openbravo.pos.spi.core;

import com.openbravo.pos.spi.localization.LocalizationProvider;
import com.openbravo.pos.spi.provider.ConfigProperty;
import com.openbravo.pos.spi.provider.PropertyType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Technical test suite verifying decoupling boundaries, core infrastructure routing catalog loops,
 * and lazy-loading metadata processing pipelines across the point-of-sale ecosystem.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public class PluginManagerTest {

    private PluginManager manager;

    /**
     * Initializes the central manager instance boundary before running each test case.
     */
    @BeforeEach
    public void setUp() {
        manager = PluginManager.getInstance();
    }

    /**
     * Validates that the master catalog successfully aggregates all registered classpath plugins 
     * and maps their descriptors into clean, immutable PluginInfo record nodes using the normalized getAllPlugins method.
     */
    @Test
    public void testGetAllPluginsAggregation() {
        List<PluginInfo> catalog = manager.getAllPlugins();
        
        assertNotNull(catalog, "The global master plugin catalog list must never be null");
        assertFalse(catalog.isEmpty(), "The discovery loader failed to harvest registered modules from testing boundaries");
        
        PluginInfo mockInfo = catalog.stream()
                .filter(info -> MockPluginSchema.MOCK_ID.equalsIgnoreCase(info.id()))
                .findFirst()
                .orElse(null);
                
        assertNotNull(mockInfo, "The automated scanning pipeline failed to discover the compiled test stub metadata");
        assertEquals(MockPluginSchema.class, mockInfo.schemaClass(), "Mismatched static properties schema blueprint reference");
        assertEquals(LocalizationProvider.class, mockInfo.serviceContract(), "Mismatched base service contract SPI assignment");
    }

    /**
     * Verifies that the registry pattern filters and extracts components successfully utilizing 
     * the unified URI addressing matrix blocks without compiled domain dependencies.
     */
    @Test
    public void testGetPluginBySelectorUriMatching() {
        // Enforces lookup evaluation routing matching the Mock URI prefix mapping
        List<PluginInfo> l10nPlugins = manager.getPluginBySelector("l10n:pt-CV");
        
        assertNotNull(l10nPlugins, "Filtered selector URI record arrays must never return null references");
        assertFalse(l10nPlugins.isEmpty(), "The master engine routing matrix failed to capture the matching stub URI");
        
        boolean containsMockId = l10nPlugins.stream()
                .anyMatch(info -> MockPluginSchema.MOCK_ID.equalsIgnoreCase(info.id()));
        assertTrue(containsMockId, "The URI routing pipeline failed to harvest the correct target identification signature");
    }

    /**
     * Ensures that querying the catalog by a specific unique string ID extracts the precise target metadata record wrapped in an Optional.
     */
    @Test
    public void testGetPluginByIdExtraction() {
        Optional<PluginInfo> result = manager.getPluginById(MockPluginSchema.MOCK_ID);
        
        assertNotNull(result, "The identification lookup stream must return an Optional container instead of null references");
        assertTrue(result.isPresent(), "The lookup engine failed to locate the matching plugin metadata using the unique ID signature");
        
        PluginInfo info = result.get();
        assertEquals(MockPluginSchema.MOCK_ID, info.id(), "The retrieved record ID does not match the requested signature parameter");
    }

    /**
     * Verifies that searching for non-existent string plugin IDs fails gracefully inside empty Optionals.
     */
    @Test
    public void testGetPluginByIdWithInvalidSignatureReturnsEmptyOptional() {
        Optional<PluginInfo> result = manager.getPluginById("invalid.plugin.package.id");
        assertNotNull(result, "The system pipeline must return empty Optionals instead of throwing pointers exceptions");
        assertTrue(result.isEmpty(), "The lookup query container should remain empty for unmapped target signatures");
    }

    /**
     * Verifies that filtering capabilities isolate and return exclusively the extensions bound 
     * to the requested target service contract type.
     */
    @Test
    public void testGetPluginsByServiceFiltering() {
        List<PluginInfo> l10nPlugins = manager.getPluginsByService(LocalizationProvider.class);
        
        assertNotNull(l10nPlugins, "Filtered service contract matrices must never return null references");
        assertFalse(l10nPlugins.isEmpty(), "The filtering mechanism discarded active matching contract components");
        
        boolean allMatchContract = l10nPlugins.stream()
                .allMatch(info -> LocalizationProvider.class.equals(info.serviceContract()));
        assertTrue(allMatchContract, "The filter criteria pipeline leaked mismatched service type contract descriptors");
    }

    /**
     * Validates that the manager extracts configuration parameters dynamically using pure string lookup metadata keys.
     */
    @Test
    public void testGetPluginSchemaMetadataDecoupledHarvesting() {
        List<ConfigProperty> properties = manager.getPluginSchemaMetadata(MockPluginSchema.MOCK_ID);
        
        assertNotNull(properties, "The structural schema list metadata must never be null");
        assertFalse(properties.isEmpty(), "The schema metadata discovery loop failed to capture properties from the annotation mirror");
        
        ConfigProperty prop = properties.get(0);
        assertEquals("mock.test.setting", prop.key(), "Mismatched metadata property key signature parsed from bytecode");
        assertEquals(PropertyType.SECRET, prop.type(), "The runtime validation safely mapped string descriptors to structural strong enums");
    }

    /**
     * Verifies that the automated configuration loading loop hydrations execute cleanly under dynamic storage lambda bindings.
     */
    @Test
    public void testGetFullyConfiguredPluginInstanceExecution() {
        manager.setConfigurationStore(pluginId -> {
            Map<String, String> settings = new HashMap<>();
            if (MockPluginSchema.MOCK_ID.equalsIgnoreCase(pluginId)) {
                settings.put("mock.test.setting", "SECURE_CREDENTIAL_TOKEN_123");
            }
            return settings;
        });

        LocalizationProvider provider = manager.getInstance(
                LocalizationProvider.class, MockPluginSchema.MOCK_ID);

        assertNotNull(provider, "The lifecycle execution manager failed to instantiate the pluggable node");
        
        if (provider instanceof com.openbravo.pos.spi.provider.ConfigurableProvider configurable) {
            Map<String, String> activeState = configurable.getCurrentConfiguration();
            assertEquals("SECURE_CREDENTIAL_TOKEN_123", activeState.get("mock.test.setting"), 
                    "The core manager failed to route and inject data settings into the provider lifecycle dynamically");
        }
    }
}
