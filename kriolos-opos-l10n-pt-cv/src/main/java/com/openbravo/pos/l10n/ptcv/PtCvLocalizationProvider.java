package com.openbravo.pos.l10n.ptcv;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.localization.LocalizationProvider;
import com.openbravo.pos.spi.provider.ConfigurableProvider;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.PLUGIN_ID;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.KEY_FORCE_DECIMALS;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.CURRENCY_CVE;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.LANG_PT;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.COUNTRY_CV;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.SELECTOR_CV;
import static com.openbravo.pos.l10n.ptcv.PtCvEscudoConfigSchema.SELECTOR_PT_CV;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Single-region localization provider dedicated to Cape Verde (pt-CV). Inherits
 * standardized temporal and fallback numeric formatters natively from the SPI
 * contract layer.
 *
 * @author KriolOS POS
 * @since 1.0.0
 */
@AutoService(LocalizationProvider.class)
@PluginMetadata(
        id = PLUGIN_ID,
        service = LocalizationProvider.class,
        schema = PtCvEscudoConfigSchema.class,
        selectors = {SELECTOR_PT_CV, SELECTOR_CV}
)
public class PtCvLocalizationProvider implements LocalizationProvider, ConfigurableProvider {

    private final Locale cvLocale = new Locale.Builder().setLanguage(LANG_PT).setRegion(COUNTRY_CV).build();
    private boolean forceTrailingDecimals;

    @Override
    public boolean supports(Locale locale) {
        return locale != null && COUNTRY_CV.equalsIgnoreCase(locale.getCountry());
    }

    @Override
    public Locale getLocale() {
        return this.cvLocale;
    }

    /**
     * Resolves the customized currency formatter matching Cape Verde legal
     * regulations. Overrides and shields flawed JVM/CLDR default symbol
     * behaviors by explicitly injecting the localized Escudo symbol suffix
     * representation.
     *
     * @return A tailored NumberFormat instance configured for Cape Verdean
     * Escudo (CVE) layouts.
     */
    public NumberFormat getCurrencyFormatter_NO_OP() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(cvLocale);
        nf.setCurrency(Currency.getInstance(CURRENCY_CVE));

        // Enforces fraction stripping boundaries based on operator preferences
        if (forceTrailingDecimals) {
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);
        } else {
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
        }

        // Defensive configuration shielding against broken CLDR locale data registry encodings
        if (nf instanceof DecimalFormat decimalFormat) {
            decimalFormat.setGroupingUsed(true);

            // Extracts and mutates the symbol properties array memory space
            java.text.DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();

            // OPTION 1: Forces the native traditional "$" suffix signifier 
            symbols.setCurrencySymbol("$");

            // OPTION 2: If the operator prefers the ISO-4217 token, uncomment this line:
            // symbols.setCurrencySymbol("CVE");
            // Re-injects the corrected and validated symbols matrix into the formatter layout
            decimalFormat.setDecimalFormatSymbols(symbols);

            /*
             * TECHNICAL NOTE FOR DEVELOPERS:
             * The '¤' (currency sign) symbol is a mandatory, native JDK pattern placeholder. 
             * It commands the DecimalFormat engine to dynamically inject the currency symbol 
             * configured via DecimalFormatSymbols ("$" in this context) as a rigid suffix.
             * DO NOT replace '¤' with a literal '$' directly inside applyPattern (e.g., "#,##0$"). 
             * Passing a raw unescaped '$' syntax character into the formatting pattern bounds 
             * crashes the tokenizer parsing routine, throwing a runtime IllegalArgumentException.
             *
             *
             * Standarizes the visual suffix boundary placement: #,##0 [Number] ¤ [Currency Symbol]
             */
            //decimalFormat.applyPattern("#,##0¤");
        }

        return nf;
    }
    
        /**
     * Resolves the customized currency formatter matching Cape Verde legal regulations.
     * Evaluates the active dynamic forceTrailingDecimals configuration state to switch 
     * between a traditional decimal separator layout and a whole integer suffix template.
     * 
     * @return A tailored NumberFormat instance configured for Cape Verdean Escudo layouts.
     */
    @Override
    public NumberFormat getCurrencyFormatter() {
        // Initializes a baseline number instance to prevent native CLDR prefix collisions
        NumberFormat nf = NumberFormat.getNumberInstance(cvLocale);

        if (nf instanceof DecimalFormat decimalFormat) {
            decimalFormat.setGroupingUsed(true);
            java.text.DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
            
            
            /*
             * DEFENSIVE SHIELDING AGAINST CLDR GAPS:
             * Modern OpenJDK matrices insert narrow no-break space characters (\u202F) for thousands separation.
             * This explicitly overrides grouping segments to enforce a standard dot symbol character instead.
             */
            symbols.setGroupingSeparator('.');

            if (forceTrailingDecimals) {
                // RULE 1: Traditional format with static trailing cents representation (e.g., 1.500$00)
                decimalFormat.setMinimumFractionDigits(2);
                decimalFormat.setMaximumFractionDigits(2);
                
                // Inverts the symbols matrix to force the '$' character to act as the fractional split point
                symbols.setDecimalSeparator('$');
                decimalFormat.setDecimalFormatSymbols(symbols);
                
                // Applies the locked precision fraction pattern template
                decimalFormat.applyPattern("#,##0.00");
            } else {
                // RULE 2: Modern commercial format stripped of decimals with a fixed suffix (e.g., 1.500$)
                decimalFormat.setMinimumFractionDigits(0);
                decimalFormat.setMaximumFractionDigits(0);
                
                // Assigns the '$' character to act as the legal currency text element
                symbols.setCurrencySymbol("$");
                decimalFormat.setDecimalFormatSymbols(symbols);
                
                // Re-injects the grouping dot rule to ensure numeric integrity before pattern parsing
                decimalFormat.setDecimalFormatSymbols(symbols);
                
                /*
                 * TECHNICAL COMPLIANCE BYPASS:
                 * To completely smash the persistent dynamic CLDR narrow space injection (\u202F),
                 * we deliberately avoid the native '¤' currency placeholder string token loop.
                 * Instead, we lock down a pure numeric pattern and escape the '$' sign safely 
                 * as a rigid literal text suffix utilizing character single quotes ('$').
                 */
                decimalFormat.applyPattern("#,##0'$'");
            }
        }

        return nf;
    }


    @Override
    public NumberFormat getIntegerFormatter() {
        NumberFormat nf = NumberFormat.getIntegerInstance(cvLocale);
        if (nf instanceof DecimalFormat decimalFormat) {
            decimalFormat.setGroupingUsed(true);
            java.text.DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
            /* Enforces exact uniform dot separation layout across long receipt columns */
            symbols.setGroupingSeparator('.');
            decimalFormat.setDecimalFormatSymbols(symbols);
        }
        return nf;
    }

    @Override
    public NumberFormat getDoubleFormatter() {
        NumberFormat nf = NumberFormat.getNumberInstance(cvLocale);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(4);
        if (nf instanceof DecimalFormat decimalFormat) {
            decimalFormat.setGroupingUsed(true);
            java.text.DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
            /* Shields high-precision weights scales values representation from narrow space anomalies */
            symbols.setGroupingSeparator('.');
            decimalFormat.setDecimalFormatSymbols(symbols);
        }
        return nf;
    }

    @Override
    public void configure(Map<String, String> configurations) {
        if (configurations != null) {
            this.forceTrailingDecimals = Boolean.parseBoolean(configurations.getOrDefault(KEY_FORCE_DECIMALS, "false"));
        }
    }

    @Override
    public Map<String, String> getCurrentConfiguration() {
        Map<String, String> currentSettings = new HashMap<>();
        currentSettings.put(KEY_FORCE_DECIMALS, String.valueOf(this.forceTrailingDecimals));
        return Map.copyOf(currentSettings);
    }
}
