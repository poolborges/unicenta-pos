package com.openbravo.pos.l10n.ptcv;

import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.annotation.PropertyDefinition;
import static com.openbravo.pos.spi.core.PluginCategory.L10N;

/**
 * Static metadata configuration schema definition for the Cape Verdean localization module.
 * Centralizes all geographical identifiers, financial tokens, and property keys as compilation constants.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
@PropertyDefinition(
    key = PtCvEscudoConfigSchema.KEY_FORCE_DECIMALS,
    label = "Force Trailing Decimals (.00)",
    i18nLabelKey = "label.l10n.cv.forcedecimals",
    description = "Enable this checkbox to append fixed .00 fractions to Cape Verdean Escudo values for custom ERP ledgers.",
    type = "BOOLEAN",
    defaultValue = "false"
)
public final class PtCvEscudoConfigSchema {
    
    /** Unique fully-qualified identification signature preventing classpath layout collisions */
    public static final String PLUGIN_ID = "org.kriolos.pos.l10n.cv.escudo";
    
    /** Individual literal URI string constants safely compiled into the constant pool */
    public static final String SELECTOR_PT_CV = "l10n:pt-CV";
    public static final String SELECTOR_CV = "l10n:CV";

    /** Configuration property key token used to toggle trailing fractional digit representations */
    public static final String KEY_FORCE_DECIMALS = "cv.escudo.force.decimals";

    /** ISO 4217 currency code identifier constant for the Cape Verdean Escudo */
    public static final String CURRENCY_CVE = "CVE";

    /** ISO 639 language code configuration for Portuguese */
    public static final String LANG_PT = "pt";

    /** ISO 3166-1 alpha-2 country code configuration for Cape Verde */
    public static final String COUNTRY_CV = "CV";


    /**
     * Private constructor to enforce utility class non-instantiability.
     */
    private PtCvEscudoConfigSchema() {
        throw new UnsupportedOperationException("Static metadata schema class cannot be instantiated");
    }
}
