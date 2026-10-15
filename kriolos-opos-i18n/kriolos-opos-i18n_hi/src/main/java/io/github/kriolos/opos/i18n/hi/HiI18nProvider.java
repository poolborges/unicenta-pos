package io.github.kriolos.opos.i18n.hi;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Hindi (hi) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.hi",
        service = I18nProvider.class,
        selectors = {"i18n:hi", "i18n:hin"}
)
public class HiI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("hi");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "हिन्दी";
    }
}
