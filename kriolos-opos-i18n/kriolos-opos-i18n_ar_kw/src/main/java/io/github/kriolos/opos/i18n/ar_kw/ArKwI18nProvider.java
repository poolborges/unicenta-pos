package io.github.kriolos.opos.i18n.ar_kw;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Arabic (Kuwait) (ar-KW) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.ar_kw",
        service = I18nProvider.class,
        selectors = {"i18n:ar-KW", "i18n:ar_KW"}
)
public class ArKwI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("ar-KW");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "العربية (الكويت)";
    }
}
