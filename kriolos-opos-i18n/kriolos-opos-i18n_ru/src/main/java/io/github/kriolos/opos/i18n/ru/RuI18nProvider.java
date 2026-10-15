package io.github.kriolos.opos.i18n.ru;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Russian (ru) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.ru",
        service = I18nProvider.class,
        selectors = {"i18n:ru", "i18n:rus"}
)
public class RuI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("ru");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "Русский";
    }
}
