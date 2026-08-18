package com.openbravo.pos.l10n.ptcv;

import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.annotation.PropertyDefinition;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.LANG_PT;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.COUNTRY_CV;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.KEY_FORCE_DECIMALS;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.PLUGIN_ID;
import com.openbravo.pos.spi.localization.LocalizationProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Technical unit testing layer focused on verifying Cape Verdean regulatory compliance mechanics.
 * Validates formatting boundaries, dynamic parameter configuration injections, and static metadata schema presence constraints.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public class PtCvLocalizationProviderTest {

    private PtCvLocalizationProvider provider;
    private Locale cvLocale;

    /**
     * Initializes the provider node and builds a matching locale validation context before each execution.
     */
    @BeforeEach
    public void setUp() {
        provider = new PtCvLocalizationProvider();
        cvLocale = new Locale.Builder()
                .setLanguage(LANG_PT)
                .setRegion(COUNTRY_CV)
                .build();
    }

    /**
     * Verifies that the implementation targets and allows execution boundaries mapped explicitly to Cape Verde.
     */
    @Test
    public void testSupportsValidLocaleContext() {
        assertTrue(provider.supports(cvLocale), "The provider must explicitly support the native Cape Verde locale signature");
    }

    /**
     * Ensures the matching execution routine safely discards completely unrelated country code signatures.
     */
    @Test
    public void testRejectsInvalidLocaleContext() {
        Locale unmappedLocale = new Locale.Builder().setLanguage("es").setRegion("CL").build();
        assertFalse(provider.supports(unmappedLocale), "The provider must accurately discard unsupported cross-border regions");
    }

    /**
     * Enforces the monetary formatting engine to completely strip fraction decimals for Cape Verdean Escudo transactions by default.
     */
    @Test
    public void testCurrencyFormatterFractionStrippingByDefault() {
        NumberFormat formatter = provider.getCurrencyFormatter();
        
        assertNotNull(formatter, "The retrieved currency formatter configuration instance must never be null");
        assertEquals(0, formatter.getMaximumFractionDigits(), "Cape Verdean Escudo regulations dictate zero fractional cents capacity");
        
        double testingAmount = 5250.75;
        String formattedString = formatter.format(testingAmount);
        
        assertFalse(formattedString.contains(",") || formattedString.contains(".75"), 
                "The processed commercial receipt layout output must not include decimal trailing values by default");
    }

    /**
     * Validates that runtime state mutations execute properly when properties are injected dynamically via the configure method.
     */
    @Test
    public void testCurrencyFormatterWithForcedDecimalsInjected() {
        Map<String, String> properties = new HashMap<>();
        properties.put(KEY_FORCE_DECIMALS, "true");
        
        provider.configure(properties);
        NumberFormat formatter = provider.getCurrencyFormatter();
        
        assertEquals(2, formatter.getMaximumFractionDigits(), "The formatter must force exactly two fractional decimal units");
        
        Map<String, String> runtimeState = provider.getCurrentConfiguration();
        assertEquals("true", runtimeState.get(KEY_FORCE_DECIMALS), "The internal runtime state map snapshot registry failed to update");
    }

        /**
     * Structural reflection verification confirming that metadata and property definitions exist on their respective class descriptors.
     * Enforces strict compliance with decoupled URI selector arrays and compilation-time schema boundaries.
     */
    @Test
    public void testMetadataAndPropertyAnnotationPresence() {
        // 1. Verifies PluginMetadata resides explicitly on the execution provider node
        Class<?> providerClass = PtCvLocalizationProvider.class;
        
        assertTrue(providerClass.isAnnotationPresent(PluginMetadata.class), 
                "The execution provider framework must be marked with the global PluginMetadata annotation matrix");
        
        PluginMetadata metadata = providerClass.getAnnotation(PluginMetadata.class);
        assertEquals(PLUGIN_ID, metadata.id(), "Static annotation unique qualified plugin identification mapping failure");
        assertEquals(LocalizationProvider.class, metadata.service(), "Static annotation service contract mapping failure");
        assertEquals(PtCvEscudoConfigSchema.class, metadata.schema(), "Static annotation schema class binding mapping failure");
        
        // Validates that the static URI array selectors match the compiled metadata structure exactly
        assertArrayEquals(
            new String[] { PtCvEscudoConfigSchema.SELECTOR_PT_CV, PtCvEscudoConfigSchema.SELECTOR_CV }, 
            metadata.selectors(), 
            "Static annotation domain routing metadata selectors mapping failure"
        );
        
        // 2. Verifies PropertyDefinition resides explicitly on the static layout schema descriptor
        Class<?> schemaClass = PtCvEscudoConfigSchema.class;
        
        assertTrue(schemaClass.isAnnotationPresent(PropertyDefinition.class), 
                "The schema class blueprint must define property layout structures via PropertyDefinition annotations");
                
        PropertyDefinition propDef = schemaClass.getAnnotation(PropertyDefinition.class);
        assertEquals(KEY_FORCE_DECIMALS, propDef.key(), "Property configuration key signature metadata mapping failure");
        assertEquals("BOOLEAN", propDef.type(), "Property type constraint parsing mapping failure");
    }

}
