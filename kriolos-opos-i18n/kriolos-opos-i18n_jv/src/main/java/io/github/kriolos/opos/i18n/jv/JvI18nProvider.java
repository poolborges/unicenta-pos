package io.github.kriolos.opos.i18n.jv;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Javanese (jv) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.jv",
        service = I18nProvider.class,
        selectors = {"i18n:jv", "i18n:jav"}
)
public class JvI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("jv");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "Basa Jawa";
    }
}
