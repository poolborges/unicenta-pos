package com.openbravo.pos.spi.i18n;

import com.openbravo.pos.spi.annotation.PluginContract;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Service Provider Interface (SPI) for internationalization language packs.
 * Allows POS applications to dynamically discover installed translations
 * and query locale display metadata.
 *
 * @author KriolOS POS
 * @since 1.0.0
 */
@PluginContract(category = "I18N", description = "Internationalization translation provider")
public interface I18nProvider {

    /**
     * Resolves the primary Locale supported by this translation package.
     *
     * @return The active Locale. Must never be null.
     */
    Locale getLocale();

    /**
     * Resolves the BCP 47 language tag (e.g. "pt", "en", "es", "fr", "ar", "zh-CN").
     *
     * @return The language tag string.
     */
    default String getLanguageCode() {
        return getLocale().toLanguageTag();
    }

    /**
     * Resolves the native display name for this language (e.g. "Português", "Español", "Français").
     *
     * @return A localized human-readable name of the language.
     */
    default String getDisplayName() {
        return getLocale().getDisplayName(getLocale());
    }

    /**
     * Resolves the display name relative to a specified context locale.
     *
     * @param contextLocale Target context locale used to format the language name.
     * @return The localized name in the context locale.
     */
    default String getDisplayName(Locale contextLocale) {
        return getLocale().getDisplayName(contextLocale != null ? contextLocale : getLocale());
    }

    /**
     * Evaluates if this provider handles the requested system locale or language code.
     *
     * @param locale Target locale requested by the POS core engine.
     * @return true if this provider supports the requested locale context.
     */
    default boolean supports(Locale locale) {
        if (locale == null) {
            return false;
        }
        return getLanguageCode().equalsIgnoreCase(locale.toLanguageTag())
                || getLocale().getLanguage().equalsIgnoreCase(locale.getLanguage());
    }

    /**
     * Optional convenience method to obtain a ResourceBundle for this language.
     *
     * @param baseName The base resource bundle name (e.g. "pos_messages").
     * @return The ResourceBundle instance, or null if not directly resolved.
     */
    default ResourceBundle getBundle(String baseName) {
        try {
            return ResourceBundle.getBundle(baseName, getLocale());
        } catch (Exception ex) {
            return null;
        }
    }
}
