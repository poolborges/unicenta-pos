package io.github.kriolos.opos.i18n.id;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Indonesian (id) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.id",
        service = I18nProvider.class,
        selectors = {"i18n:id", "i18n:ind"}
)
public class IdI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("id");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "Bahasa Indonesia";
    }
}
