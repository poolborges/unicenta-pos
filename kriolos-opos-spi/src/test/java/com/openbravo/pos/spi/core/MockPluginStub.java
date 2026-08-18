package com.openbravo.pos.spi.core;

import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.annotation.PropertyDefinition;
import com.openbravo.pos.spi.provider.ConfigurableProvider;
import com.openbravo.pos.spi.localization.LocalizationProvider;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

/**
 * Static metadata configuration schema container simulating third-party extensions.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
@PropertyDefinition(
    key = "mock.test.setting",
    label = "Mock Setting Label",
    i18nLabelKey = "label.mock.test.setting",
    description = "A testing property definition block",
    type = "SECRET",
    required = true
)
class MockPluginSchema {
    /** Unique fully-qualified identification signature for the test driver instance boundary */
    public static final String MOCK_ID = "org.kriolos.pos.mock.test.plugin";
    
    private MockPluginSchema() {}
}

/**
 * Concrete test stub execution driver emulating pluggable core lifecycle providers.
 * Modernized to map unified URI addressing structures inside testing boundaries.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
@PluginMetadata(
    id = MockPluginSchema.MOCK_ID, 
    schema = MockPluginSchema.class, 
    service = LocalizationProvider.class,
    selectors = {"l10n:pt-CV", "l10n:CV"} // <-- CORREÇÃO: Alinhado perfeitamente com a busca por URI do PluginManagerTest
)
public class MockPluginStub implements LocalizationProvider, ConfigurableProvider {

    private String injectedValue;

    @Override
    public boolean supports(Locale locale) {
        return locale != null && "ZZ".equalsIgnoreCase(locale.getCountry());
    }

    @Override
    public void configure(Map<String, String> configurations) {
        if (configurations != null) {
            this.injectedValue = configurations.get("mock.test.setting");
        }
    }

    @Override
    public Map<String, String> getCurrentConfiguration() {
        Map<String, String> snapshot = new HashMap<>();
        snapshot.put("mock.test.setting", this.injectedValue);
        return Map.copyOf(snapshot);
    }

    @Override public NumberFormat getCurrencyFormatter() { return null; }
    @Override public DateFormat getDateFormatter() { return null; }
    @Override public NumberFormat getNumberFormatter() { return null; }
}
