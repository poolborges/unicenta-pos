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
    }

    public static void applySystemProperties(AppConfig config) {
        applySystemProperties((BootstrapConfig) config);
    }

    public static void applyPosTheme(String themeId) {
        POSThemeManager.applyTheme(themeId);
    }
}
