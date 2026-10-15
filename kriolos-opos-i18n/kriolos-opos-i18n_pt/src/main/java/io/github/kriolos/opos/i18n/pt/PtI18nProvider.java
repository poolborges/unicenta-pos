package io.github.kriolos.opos.i18n.pt;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Portuguese internationalization provider (pt).
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.pt",
        service = I18nProvider.class,
        selectors = {"i18n:pt", "i18n:por"}
)
public class PtI18nProvider implements I18nProvider {

    private static final Locale PT_LOCALE = Locale.forLanguageTag("pt");

    @Override
    public Locale getLocale() {
        return PT_LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "Português";
    }
}
