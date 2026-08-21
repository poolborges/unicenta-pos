package com.openbravo.pos.spi.localization;

import com.openbravo.pos.spi.core.PluginManager;
import com.openbravo.pos.spi.provider.ProviderFactory;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Centralized domain factory registry responsible for managing and producing localization extensions.
 * Implements the unified ProviderFactory contract to guarantee consistent architectural routing
 * while leveraging the generic core PluginManager to automate dynamic runtime settings injection.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public final class LocalizationFactory implements ProviderFactory<Locale, LocalizationProvider> {
    
    private static final Logger LOGGER = Logger.getLogger(LocalizationFactory.class.getName());

    private static LocalizationFactory instance;

    /**
     * Enforces private constructor synchronization for singleton layout compliance.
     */
    private LocalizationFactory() {
        // Protected constructor hiding implicit public visibility instantiations
    }

    /**
     * Thread-safe access pattern to acquire the system global localization factory broker.
     * 
     * @return The active LocalizationFactory instance.
     */
    public static synchronized LocalizationFactory getInstance() {
        if (instance == null) {
            instance = new LocalizationFactory();
        }
        return instance;
    }

    /**
     * Factory execution node that resolves, instantiates, and configures the target localization module.
     * Executes a cascade-fallthrough routing strategy across multi-tier URI selectors to maximize 
     * matching thresholds before fallback initialization triggers.
     * 
     * @param targetLocale The primary geographic locale configuration requested by the application.
     * @return A specialized LocalizationProvider or a baseline standard default execution framework.
     */
    @Override
    public LocalizationProvider getProvider(Locale targetLocale) {
        LOGGER.info("Search Localization SPI for: "+targetLocale.toLanguageTag());
        
        if (targetLocale == null || targetLocale.getCountry().isEmpty()) {
            return new FallbackLocalizationProvider(Locale.getDefault());
        }

        String languageToken = targetLocale.getLanguage().toLowerCase().trim();
        String countryToken = targetLocale.getCountry().toUpperCase().trim();
        PluginManager manager = PluginManager.getInstance();

        // CASCADE LEVEL 1: Specific Dialect Region Matching Selector (e.g., "l10n:pt-CV")
        if (!languageToken.isEmpty()) {
            String fullLocaleUri = "l10n:" + languageToken + "-" + countryToken;
            LocalizationProvider dialectProvider = manager.getInstanceBySelector(LocalizationProvider.class, fullLocaleUri);
            if (dialectProvider != null) {
                return dialectProvider;
            }
        }

        // CASCADE LEVEL 2: General Sovereign Country Matching Selector (e.g., "l10n:CV")
        String standaloneCountryUri = "l10n:" + countryToken;
        LocalizationProvider countryProvider = manager.getInstanceBySelector(LocalizationProvider.class, standaloneCountryUri);
        if (countryProvider != null) {
            return countryProvider;
        }

        // CASCADE LEVEL 3: Failsafe system boundary infrastructure alternative activation
        return new FallbackLocalizationProvider(targetLocale);
    }
}
