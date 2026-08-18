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
import java.text.DateFormat;
import java.util.Locale;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

/**
 * Single-region localization provider dedicated to Cape Verde (pt-CV).
 * Consumes a fully encapsulated constant matrix from the static schema boundary to build regional formats.
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
    public NumberFormat getCurrencyFormatter() {
        NumberFormat nf = NumberFormat.getCurrencyInstance(cvLocale);
        nf.setCurrency(Currency.getInstance(CURRENCY_CVE));

        if (forceTrailingDecimals) {
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);
        } else {
            nf.setMinimumFractionDigits(0);
            nf.setMaximumFractionDigits(0);
        }

        if (nf instanceof DecimalFormat decimalFormat) {
            decimalFormat.setGroupingUsed(true);
        }

        return nf;
    }

    @Override public DateFormat getDateFormatter() { return DateFormat.getDateInstance(DateFormat.MEDIUM, cvLocale); }
    @Override public NumberFormat getNumberFormatter() { return NumberFormat.getNumberInstance(cvLocale); }

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
