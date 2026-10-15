package io.github.kriolos.opos.i18n.es;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Spanish (es) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.es",
        service = I18nProvider.class,
        selectors = {"i18n:es", "i18n:spa"}
)
public class EsI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("es");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "Español";
    }
}
