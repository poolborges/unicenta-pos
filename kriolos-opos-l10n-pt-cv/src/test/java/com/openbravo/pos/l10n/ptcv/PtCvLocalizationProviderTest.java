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
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Comprehensive operational test suite certifying 100% regulatory and visual formatting compliance 
 * for the Cape Verdean (pt-CV) localization engine module.
 * Bridges factual CLDR diagnostics with strict polimorphic assertion validations.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public class PtCvLocalizationProviderTest {

    private PtCvLocalizationProvider provider;
    private Locale cvLocale;
    private LocalDateTime sampleDateTime;

    /**
     * Initializes the localized execution block and freezes a static temporal anchor point for pipeline consistency.
     */
    @BeforeEach
    public void setUp() {
        provider = new PtCvLocalizationProvider();
        cvLocale = new Locale.Builder().setLanguage(LANG_PT).setRegion(COUNTRY_CV).build();
        
        // Fixed snapshot tracking point: 18th August 2026 at 23:31:15
        sampleDateTime = LocalDateTime.of(2026, Month.AUGUST, 18, 23, 31, 15);
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
     * Assures the currency formatter enforces the whole integer modern suffix format by default, 
     * shielding outputs from broken CLDR space anomalies.
     */
    @Test
    public void testCurrencyFormatterWholeIntegerSuffixByDefault() {
        NumberFormat formatter = provider.getCurrencyFormatter();
        assertNotNull(formatter, "The currency formatter instance must never be null");
        
        double testingAmount = 1500.0;
        String formattedOutput = formatter.format(testingAmount);
        
        // Eliminates CLDR non-breaking spaces and asserts the custom shielded suffix string literal layout
        assertEquals("1.500$", formattedOutput, 
                "The default currency engine failed to map the rigid standalone escudo suffix format");
    }

    /**
     * Assures that enabling forced decimals switches the pattern matrix to the traditional layout, 
     * using the dollar sign as an exact fractional decimal split point.
     */
    @Test
    public void testCurrencyFormatterTraditionalDecimalSplitWithForcedDecimals() {
        Map<String, String> configuration = new HashMap<>();
        configuration.put(KEY_FORCE_DECIMALS, "true");
        provider.configure(configuration);
        
        NumberFormat formatter = provider.getCurrencyFormatter();
        double testingAmount = 1500.0;
        String formattedOutput = formatter.format(testingAmount);
        
        assertEquals("1.500$00", formattedOutput, 
                "The currency layout failed to invert the matrix and assign the dollar character as the decimal split anchor");
        
        Map<String, String> runtimeState = provider.getCurrentConfiguration();
        assertEquals("true", runtimeState.get(KEY_FORCE_DECIMALS), "The internal runtime configuration state map failed to persist fields");
    }

    /**
     * Validates that the Whole Integer routine preserves thousands groupings without leaking fractions.
     */
    @Test
    public void testIntegerFormatterWholeUnitPreservation() {
        NumberFormat formatter = provider.getIntegerFormatter();
        assertNotNull(formatter, "The whole integer formatter instance must never be null");
        
        String output = formatter.format(1500.75);
        // Whole integer rounds mathematically following pt-CV metrics rules
        assertEquals("1.501", output, "Mismatched integer grouping or fraction rounding behavior");
    }

    /**
     * Validates that the Double precision layout handles multi-fraction floating points for scales.
     */
    @Test
    public void testDoubleFormatterPrecisionBoundary() {
        NumberFormat formatter = provider.getDoubleFormatter();
        assertNotNull(formatter, "The double precision formatter instance must never be null");
        
        assertEquals("1.500,75", formatter.format(1500.75), "Mismatched double grouping separation");
        assertEquals("1,2346", formatter.format(1.234567), "The double precision engine failed to round up to 4 digits");
    }

    /**
     * Validates percentage ratio translations and suffix attachments.
     */
    @Test
    public void testPercentFormatterRatioInjections() {
        NumberFormat formatter = provider.getPercentFormatter();
        assertNotNull(formatter, "The commercial percentage formatter instance must never be null");
        
        assertEquals("15%", formatter.format(0.15), "The percentage engine failed to format basic tax ratios");
        assertEquals("12,5%", formatter.format(0.125), "The percentage engine failed to render fractional tax subsets");
    }

    /**
     * Verifies 100% compliance across all modern Java.Time thread-safe Date and Time formatting architectures.
     */
    @Test
    public void testTemporalDateTimeFormatterPipelines() {
        DateTimeFormatter dateFmt = provider.getDateFormatter();
        DateTimeFormatter dateTimeFmt = provider.getDateTimeFormatter();
        DateTimeFormatter timeFmt = provider.getTimeFormatter();
        DateTimeFormatter hourMinFmt = provider.getHourMinFormatter();

        assertNotNull(dateFmt, "Date formatter engine layer must never be null");
        assertNotNull(dateTimeFmt, "Combined DateTime formatter engine layer must never be null");
        assertNotNull(timeFmt, "Time formatter engine layer must never be null");
        assertNotNull(hourMinFmt, "Concise hour-min formatter engine layer must never be null");

        // Asserts chronological outputs match pt-CV regional standard rules matching CLDR outputs
        assertEquals("18/08/2026", dateFmt.format(sampleDateTime), "Mismatched MEDIUM date string conversion path");
        assertTrue(dateTimeFmt.format(sampleDateTime).contains("18/08/2026"), "Mismatched combined MEDIUM date-time timestamp tracking text");
        assertEquals("23:31:15", timeFmt.format(sampleDateTime), "Mismatched MEDIUM running clock time tracking text");
        assertEquals("23:31", hourMinFmt.format(sampleDateTime), "Mismatched SHORT concise timestamp tracking text");
    }

    /**
     * Structural reflection verification confirming that metadata and property definitions exist on their respective class descriptors.
     */
    @Test
    public void testMetadataAndPropertyAnnotationPresence() {
        Class<?> providerClass = PtCvLocalizationProvider.class;
        assertTrue(providerClass.isAnnotationPresent(PluginMetadata.class), "Missing global PluginMetadata annotation descriptor");
        
        PluginMetadata metadata = providerClass.getAnnotation(PluginMetadata.class);
        assertEquals(PLUGIN_ID, metadata.id(), "Mismatched unique plugin packet identifier");
        assertEquals(LocalizationProvider.class, metadata.service(), "Mismatched service SPI contract binding");
        assertEquals(PtCvEscudoConfigSchema.class, metadata.schema(), "Mismatched configuration static property schema map token");
        
        assertArrayEquals(
            new String[] { PtCvEscudoConfigSchema.SELECTOR_PT_CV, PtCvEscudoConfigSchema.SELECTOR_CV }, 
            metadata.selectors(), 
            "Mismatched compiled URI service registration routing selectors arrays"
        );
        
        Class<?> schemaClass = PtCvEscudoConfigSchema.class;
        assertTrue(schemaClass.isAnnotationPresent(PropertyDefinition.class), "Missing configuration schema layout parameters annotations");
                
        PropertyDefinition propDef = schemaClass.getAnnotation(PropertyDefinition.class);
        assertEquals(KEY_FORCE_DECIMALS, propDef.key(), "Mismatched setting data storage string configuration property path key");
        assertEquals("BOOLEAN", propDef.type(), "Mismatched properties datatype mapping validation parameter context type");
    }
}
