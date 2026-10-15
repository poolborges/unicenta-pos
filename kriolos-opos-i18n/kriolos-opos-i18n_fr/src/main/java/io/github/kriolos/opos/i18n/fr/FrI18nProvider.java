package io.github.kriolos.opos.i18n.fr;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * French (fr) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.fr",
        service = I18nProvider.class,
        selectors = {"i18n:fr", "i18n:fra"}
)
public class FrI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("fr");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "Français";
    }
}
