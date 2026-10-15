package com.openbravo.pos.spi.i18n;

import com.openbravo.pos.spi.core.PluginManager;
import com.openbravo.pos.spi.provider.ProviderFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * Centralized domain factory registry responsible for managing and discovering
 * internationalization language packs.
 *
 * @author KriolOS POS
 * @since 1.0.0
 */
public final class I18nFactory implements ProviderFactory<Locale, I18nProvider> {

    private static final Logger LOGGER = Logger.getLogger(I18nFactory.class.getName());

    private static I18nFactory instance;

    private I18nFactory() {
    }

    /**
     * Acquires the singleton instance of I18nFactory.
     *
     * @return The active I18nFactory instance.
     */
    public static synchronized I18nFactory getInstance() {
        if (instance == null) {
            instance = new I18nFactory();
        }
        return instance;
    }

    /**
     * Resolves an I18nProvider matching the requested target Locale.
     *
     * @param targetLocale Target Locale.
     * @return Matching I18nProvider, or null if no provider was found.
     */
    @Override
    public I18nProvider getProvider(Locale targetLocale) {
        final Locale target = targetLocale != null ? targetLocale : Locale.getDefault();

        LOGGER.fine(() -> "Searching I18n SPI for: " + target.toLanguageTag());

        PluginManager manager = PluginManager.getInstance();

        // Level 1: Full locale tag selector lookup (e.g., "i18n:pt-CV", "i18n:zh-CN")
        String fullTag = target.toLanguageTag();
        I18nProvider providerByTag = manager.getInstanceBySelector(I18nProvider.class, "i18n:" + fullTag);
        if (providerByTag != null) {
            return providerByTag;
        }

        // Level 2: Language-only selector lookup (e.g., "i18n:pt", "i18n:es")
        String langToken = target.getLanguage();
        if (!langToken.isEmpty()) {
            I18nProvider providerByLang = manager.getInstanceBySelector(I18nProvider.class, "i18n:" + langToken);
            if (providerByLang != null) {
                return providerByLang;
            }
        }

        // Level 3: Fallback scan through all available providers using supports(locale)
        for (I18nProvider provider : getAvailableProviders()) {
            if (provider.supports(target)) {
                return provider;
            }
        }

        return null;
    }

    /**
     * Scans and returns all registered I18nProvider extensions found on classpath.
     *
     * @return An unmodifiable List of sorted I18nProvider instances.
     */
    public List<I18nProvider> getAvailableProviders() {
        List<I18nProvider> providers = new ArrayList<>();
        ServiceLoader<I18nProvider> loader = ServiceLoader.load(I18nProvider.class);
        for (I18nProvider p : loader) {
            providers.add(p);
        }
        providers.sort(Comparator.comparing(I18nProvider::getDisplayName, String.CASE_INSENSITIVE_ORDER));
        return Collections.unmodifiableList(providers);
    }
}
