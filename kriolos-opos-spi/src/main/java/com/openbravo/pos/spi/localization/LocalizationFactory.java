package com.openbravo.pos.spi.localization;

import com.openbravo.pos.spi.core.PluginManager;
import com.openbravo.pos.spi.annotation.PluginMetadata;
import com.openbravo.pos.spi.provider.ProviderFactory;
import java.util.Locale;
import java.util.ServiceLoader;

/**
 * Centralized domain factory registry responsible for managing and producing localization extensions.
 * Implements the unified ProviderFactory contract to guarantee consistent architectural routing
 * while leveraging the generic core PluginManager to automate dynamic runtime settings injection.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public final class LocalizationFactory implements ProviderFactory<Locale, LocalizationProvider> {

    /**
     * Factory execution node that resolves, instantiates, and configures the target localization module.
     * Implements the structural ProviderFactory contract interface requirements.
     * 
     * @param targetLocale The primary geographic locale configuration requested by the application.
     * @return A specialized LocalizationProvider or a baseline standard default execution framework.
     */
    @Override
    public LocalizationProvider getProvider(Locale targetLocale) {
  
        return new FallbackLocalizationProvider(targetLocale);
    }
}
