/*
 * Copyright (C) 2023 Paulo Borges
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
package com.openbravo.pos.spi.localization;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Locale;


/**
 * Standard internal generic execution layer providing localized formatting
 * default constants.
 */
class FallbackLocalizationProvider implements LocalizationProvider {

    private final Locale defaultLocale;

    FallbackLocalizationProvider(Locale locale) {
        this.defaultLocale = locale != null ? locale : Locale.getDefault();
    }

    @Override
    public boolean supports(Locale locale) {
        return true;
    }

    @Override
    public NumberFormat getCurrencyFormatter() {
        return NumberFormat.getCurrencyInstance(defaultLocale);
    }

    @Override
    public DateFormat getDateFormatter() {
        return DateFormat.getDateInstance(DateFormat.MEDIUM, defaultLocale);
    }

    @Override
    public NumberFormat getNumberFormatter() {
        return NumberFormat.getNumberInstance(defaultLocale);
    }
}
