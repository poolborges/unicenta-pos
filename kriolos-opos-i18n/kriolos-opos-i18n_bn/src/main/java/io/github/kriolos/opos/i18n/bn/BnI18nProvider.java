package io.github.kriolos.opos.i18n.bn;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Bengali (bn) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.bn",
        service = I18nProvider.class,
        selectors = {"i18n:bn", "i18n:ben"}
)
public class BnI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("bn");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "বাংলা";
    }
}
