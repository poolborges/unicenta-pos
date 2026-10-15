package io.github.kriolos.opos.i18n.zh_cn;

import com.google.auto.service.AutoService;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.i18n.I18nProvider;
import java.util.Locale;

/**
 * Chinese (Simplified) (zh-CN) internationalization provider.
 */
@AutoService(I18nProvider.class)
@PluginMetadata(
        id = "io.github.kriolos.opos.i18n.zh_cn",
        service = I18nProvider.class,
        selectors = {"i18n:zh-CN", "i18n:zh_CN", "i18n:zho"}
)
public class ZhCnI18nProvider implements I18nProvider {

    private static final Locale LOCALE = Locale.forLanguageTag("zh-CN");

    @Override
    public Locale getLocale() {
        return LOCALE;
    }

    @Override
    public String getDisplayName() {
        return "中文 (简体)";
    }
}
