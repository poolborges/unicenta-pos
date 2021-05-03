/*
 * Copyright (C) 2020 Paulo Borges
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
package io.github.kriolos.krpos.sample;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 *
 * @author pauloborges
 */
public class LocaleExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        Locale defaultLocale = Locale.getDefault();
        
        System.out.println("getCountry: "+defaultLocale.getCountry());
        System.out.println(""+defaultLocale.getDisplayCountry());
        System.out.println(""+defaultLocale.getDisplayLanguage());
        System.out.println("getDisplayName: "+defaultLocale.getDisplayName());
        System.out.println(""+defaultLocale.getDisplayScript());
        System.out.println(""+defaultLocale.getDisplayVariant());
        System.out.println("getISO3Country: "+defaultLocale.getISO3Country());
        System.out.println(""+defaultLocale.getISO3Language());
        System.out.println(""+defaultLocale.getLanguage());
        System.out.println(""+defaultLocale.getScript());
        System.out.println("getScript: "+defaultLocale.getScript());
        
        System.out.println("NumberFormat.getAvailableLocales() ==============================");
        Locale[] numberFormatLocales = NumberFormat.getAvailableLocales();
        Arrays.stream(numberFormatLocales).forEach((locale) -> {
            System.out.println(""+locale.getDisplayName());
        });
        
        System.out.println("DateFormat.getAvailableLocales() ==============================");
	Locale[] dateFormatLocales = DateFormat.getAvailableLocales();
        Arrays.stream(dateFormatLocales).forEach((locale) -> {
            System.out.println(""+locale.getDisplayName());
        });
        
        
        System.out.println("Locale.getAvailableLocales() ==============================");
	Locale[] locales = Locale.getAvailableLocales();
        Arrays.stream(locales).forEach((locale) -> {
            System.out.println(""+locale.getDisplayName());
        });
    }
    
}
