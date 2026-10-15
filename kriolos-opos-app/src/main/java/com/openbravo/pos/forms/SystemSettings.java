/*
 * Copyright (C) 2026 KriolOS
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.openbravo.pos.forms;

import com.openbravo.pos.config.AppConfig;
import com.openbravo.format.Formats;
import com.openbravo.pos.spi.config.BootstrapConfig;
import com.openbravo.pos.spi.localization.LocalizationFactory;
import com.openbravo.pos.spi.localization.LocalizationProvider;
import com.openbravo.pos.ui.api.POSThemeManager;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dev
 */
public class SystemSettings {

    private static final Logger LOGGER = Logger.getLogger(SystemSettings.class.getName());

    public static void applySystemProperties(BootstrapConfig config) {
        // Set the look and feel.
        String newThemeProperty = config.getUiTheme();
        applyPosTheme(newThemeProperty);

        // Localization configuration switch
        String localizationMode = config.getLocalizationMode();
        if ("provider".equalsIgnoreCase(localizationMode)) {
            applyLocalizationProvider(config);
        } else {
            applyLocalizationLegacy(config);
        }
    }

    public static void applySystemProperties(AppConfig config) {
        applySystemProperties((BootstrapConfig) config);
    }

    public static void applyPosTheme(String themeId) {
        POSThemeManager.applyTheme(themeId);
    }

    private static void applyLocalizationLegacy(BootstrapConfig config) {

        LOGGER.log(Level.INFO, "Localization from legacy properties");

        Locale startupLocale = config.getStartupLocale();
        Locale.setDefault(startupLocale);

        LOGGER.log(Level.INFO, "Localization locale default: " + Locale.getDefault().toLanguageTag());

        //Set Format/Pattern for: Number, Date, Currency 
        Formats.setIntegerPattern(config.getIntegerPattern());
        Formats.setDoublePattern(config.getDoublePattern());
        Formats.setCurrencyPattern(config.getCurrencyPattern());
        Formats.setPercentPattern(config.getPercentPattern());
        Formats.setDatePattern(config.getDatePattern());
        Formats.setTimePattern(config.getTimePattern());
        Formats.setDateTimePattern(config.getDateTimePattern());
    }

    private static void applyLocalizationProvider(BootstrapConfig config) {

        LOGGER.log(Level.INFO, "Localization from SPI Provider");

        // Build target locale from "localization.locale" (ex: "pt-CV")
        Locale targetLocale = parseLocaleTag(config.getLocalizationLocale());
        try {

            Locale.setDefault(targetLocale);

            LocalizationProvider provider = LocalizationFactory.getInstance().getProvider(targetLocale);

            //Set Format/Pattern for: Number, Date, Currency 
            Formats.setIntegerFormatter(provider.getIntegerFormatter());
            Formats.setDoubleFormat(provider.getDoubleFormatter());
            Formats.setCurrencyFormat(provider.getCurrencyFormatter());
            Formats.setPercentFormatter(provider.getPercentFormatter());
            Formats.setDateFormat(provider.getDateFormatter());
            Formats.setTimeFormatter(provider.getTimeFormatter());
            Formats.setDateTimeFormatter(provider.getDateTimeFormatter());

            LOGGER.log(Level.INFO, "Localization provider applied: {0} for locale: {1}",
                    new Object[]{provider.getClass().getSimpleName(), targetLocale.toLanguageTag()});
        }
        catch (Exception e) {
            LOGGER.log(Level.WARNING, "SPI localization failed, falling back to JDK defaults", e);
        }
    }

    /**
     * Parses a BCP 47 / IETF language tag like "pt-CV" into a Locale. Returns
     * system default if null/empty.
     */
    private static Locale parseLocaleTag(String tag) {
        if (tag == null || tag.isBlank()) {
            return Locale.getDefault();
        }
        return Locale.forLanguageTag(tag);
    }

    private static boolean isNullOrBlank(String text) {
        return text == null || text.isBlank();
    }
}
