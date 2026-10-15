package io.github.kriolos.opos.i18n.ar;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Arabic (ar) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.ar",
        service = I18nProvider.class,
        selectors = {"i18n:ar", "i18n:ara"}
)
public class ArI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("ar");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "العربية";
    }
}
