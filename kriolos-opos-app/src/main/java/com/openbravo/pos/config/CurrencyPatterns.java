/*
 * Copyright (C) 2026 Paulo Borges
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
package com.openbravo.pos.config;

import java.util.List;

/**
 * Centralized metadata utility class providing an unmodifiable matrix of currency formatting patterns.
 * Designed to safely populate user interface combobox components (e.g., jcboCurrency) across the POS core.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public final class CurrencyPatterns {

    /**
     * Standard international prefix layout with a dynamic currency placeholder and two decimal units.
     * <p><strong>Behavior:</strong> Dynamic placeholder (¤) at the beginning.</p>
     * <p><strong>Example Output (USD):</strong> {@code $1,500.00}</p>
     * <p><strong>Commercial Case:</strong> US / USD International Standard.</p>
     */
    public static final String PREFIX_DYNAMIC_TWO_DECIMALS = "\u00A4 #,##0.00";

    /**
     * Traditional accounting prefix forcing a hardcoded single space separation and locked decimals.
     * <p><strong>Behavior:</strong> Dynamic placeholder (¤) with an escaped literal space.</p>
     * <p><strong>Example Output (EUR):</strong> {@code € 1.500,00}</p>
     * <p><strong>Commercial Case:</strong> Classic European Accounting Ledger.</p>
     */
    public static final String PREFIX_DYNAMIC_WITH_SPACE_TWO_DECIMALS = "\u00A4 '# '##0.00";

    /**
     * Standard international suffix layout trailing the dynamic currency placeholder after a literal space.
     * <p><strong>Behavior:</strong> Dynamic placeholder (¤) positioned at the end.</p>
     * <p><strong>Example Output (EUR):</strong> {@code 1.500,00 €}</p>
     * <p><strong>Commercial Case:</strong> Native Eurozone Standard Layout (PT/ES/FR).</p>
     */
    public static final String SUFFIX_DYNAMIC_WITH_SPACE_TWO_DECIMALS = "#,##0.00 \u00A4";

    /**
     * Dynamic localized currency symbol attached directly as a suffix to whole numbers.
     * <p><strong>Behavior:</strong> Dynamic placeholder (¤) attached at the end without spaces.</p>
     * <p><strong>Example Output (CVE):</strong> {@code 1.500$}</p>
     * <p><strong>Commercial Case:</strong> Dynamic Standalone Suffix Layout.</p>
     */
    public static final String SUFFIX_DYNAMIC_WHOLE_NUMBER = "#,##0\u00A4";

    /**
     * Rigid currency sign symbol forced strictly as a prefix string literal with a single space.
     * <p><strong>Behavior:</strong> Hardcoded '$' literal token character text at the beginning (Shielded from CLDR alterations).</p>
     * <p><strong>Example Output:</strong> {@code $ 1.500,00}</p>
     * <p><strong>Commercial Case:</strong> Protected Dollar Sign Prefix Layout.</p>
     */
    public static final String PREFIX_HARDCODED_DOLLAR_TWO_DECIMALS = "'$' #,##0.00";

    /**
     * Rigid Euro currency sign symbol forced strictly as a prefix string literal with a single space.
     * <p><strong>Behavior:</strong> Hardcoded '€' literal token character text at the beginning (Shielded from CLDR alterations).</p>
     * <p><strong>Example Output:</strong> {@code € 1.500,00}</p>
     * <p><strong>Commercial Case:</strong> Protected Euro Sign Prefix Layout.</p>
     */
    public static final String PREFIX_HARDCODED_EURO_TWO_DECIMALS = "'\u20AC' #,##0.00";

    /**
     * Rigid Euro currency sign symbol forced strictly as a suffix string literal trailing a single space.
     * <p><strong>Behavior:</strong> Hardcoded '€' literal token character text at the end (Shielded from CLDR alterations).</p>
     * <p><strong>Example Output:</strong> {@code 1.500,00 €}</p>
     * <p><strong>Commercial Case:</strong> Protected Euro Sign Suffix Layout.</p>
     */
    public static final String SUFFIX_HARDCODED_EURO_TWO_DECIMALS = "#,##0.00 '\u20AC'";

    /**
     * Rigid uniform placeholder suffix often utilized to render non-monetary discrete inventories.
     * <p><strong>Behavior:</strong> Hardcoded 'Units' text string literal suffix token.</p>
     * <p><strong>Example Output:</strong> {@code 1.500 Units}</p>
     * <p><strong>Commercial Case:</strong> Discrete Physical Stock Quantity Metrics Layout.</p>
     */
    public static final String SUFFIX_HARDCODED_UNITS_WHOLE_NUMBER = "#,##0 'Units'";

    /**
     * Minimalist fractionless whole number format with a locked literal text escudo character suffix.
     * <p><strong>Behavior:</strong> Hardcoded '$' literal text suffix token (Completely shields layout from JVM CLDR bugs).</p>
     * <p><strong>Example Output (CVE):</strong> {@code 1.500$}</p>
     * <p><strong>Commercial Case:</strong> Cape Verdean Escudo Modern Commercial Retail Ledger.</p>
     */
    public static final String SUFFIX_HARDCODED_CVE_WHOLE_NUMBER = "#,##0'$'";

    /**
     * Cape Verdean traditional accounting layout forcing the dollar sign to act as the exact decimal split point.
     * <p><strong>Behavior:</strong> Pattern relying on custom runtime overrides (Requires DecimalFormatSymbols.setDecimalSeparator('$')).</p>
     * <p><strong>Example Output (CVE):</strong> {@code 1.500$00}</p>
     * <p><strong>Commercial Case:</strong> Cape Verdean Escudo Regulatory Fiscal Legacy Ledger.</p>
     */
    public static final String INVERTED_DECIMAL_CVE_TWO_DECIMALS = "#,##0.00";

    /**
     * Fractional micro-currencies pattern forcing high-precision layouts alongside an escaped Euro literal suffix.
     * <p><strong>Behavior:</strong> Four static locked decimal digits with a literal Euro character string suffix.</p>
     * <p><strong>Example Output (EUR):</strong> {@code 1.500,0000 €}</p>
     * <p><strong>Commercial Case:</strong> High-Accuracy Fuel Stations Pricing, Raw Material Invoicing, or Wholesale Ledgers.</p>
     */
    public static final String SUFFIX_HARDCODED_EURO_FOUR_DECIMALS = "#,##0.0000 '\u20AC'";

    /**
     * Legacy accounting whole number compliance representation forcing leading zero masks.
     * <p><strong>Behavior:</strong> Hardcoded '$' text string literal suffix token trailing a fixed width zero mask.</p>
     * <p><strong>Example Output:</strong> {@code 1500 $}</p>
     * <p><strong>Commercial Case:</strong> Fixed Width Legacy Numeric Sequence Mask Layout.</p>
     */
    public static final String SUFFIX_HARDCODED_DOLLAR_FIXED_MASK = "#00 '$'";

    /**
     * Immutable collection block containing all compiled currency pattern strings in optimized routing order.
     */
    private static final List<String> PATTERNS = List.of(
        PREFIX_DYNAMIC_TWO_DECIMALS,
        PREFIX_DYNAMIC_WITH_SPACE_TWO_DECIMALS,
        SUFFIX_DYNAMIC_WITH_SPACE_TWO_DECIMALS,
        SUFFIX_DYNAMIC_WHOLE_NUMBER,
        PREFIX_HARDCODED_DOLLAR_TWO_DECIMALS,
        PREFIX_HARDCODED_EURO_TWO_DECIMALS,
        SUFFIX_HARDCODED_EURO_TWO_DECIMALS,
        SUFFIX_HARDCODED_UNITS_WHOLE_NUMBER,
        SUFFIX_HARDCODED_CVE_WHOLE_NUMBER,
        INVERTED_DECIMAL_CVE_TWO_DECIMALS,
        SUFFIX_HARDCODED_EURO_FOUR_DECIMALS,
        SUFFIX_HARDCODED_DOLLAR_FIXED_MASK
    );

    /**
     * Private constructor to enforce utility class non-instantiability boundaries.
     */
    private CurrencyPatterns() {
        throw new UnsupportedOperationException("Static metadata utility class cannot be instantiated");
    }

    /**
     * Returns an immutable List containing all standard currency format pattern strings available for UI injection.
     * 
     * @return An unmodifiable List of pattern Strings.
     */
    public static List<String> getAllPatterns() {
        return PATTERNS;
    }
}
