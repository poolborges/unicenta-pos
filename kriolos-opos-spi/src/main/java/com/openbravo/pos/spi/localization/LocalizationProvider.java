package com.openbravo.pos.spi.localization;

import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;

/**
 * Service Provider Interface (SPI) for point-of-sale regional localization extensions.
 * Encapsulates transactional formatting patterns and localized extension hooks.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public interface LocalizationProvider {
    
    /**
     * Evaluates if this provider handles the requested system locale context.
     * 
     * @param locale The active runtime locale injected by the POS core engine.
     * @return true if this provider supports the given locale context threshold.
     */
    boolean supports(Locale locale);

    /**
     * Resolves the primary native geographic locale bound to this provider module.
     * 
     * @return The active regional Locale token. Must never be null.
     */
    Locale getLocale();
    
    /**
     * Resolves the customized currency formatter matching the target region legal rules.
     * 
     * @return A tailored NumberFormat instance configured for regional currency.
     */
    default NumberFormat getCurrencyFormatter(){
        return NumberFormat.getCurrencyInstance(getLocale());
    }

    /**
     * Resolves the general decimal and thousands grouping numeric layout context.
     * 
     * @return A standard NumberFormat built for local general numeric representations.
     */
    default NumberFormat getNumberFormatter() {
        return NumberFormat.getNumberInstance(getLocale());
    }

    /**
     * Resolves a rigid integer formatter completely stripped of fractional trailing units.
     * 
     * @return A NumberFormat instance configured strictly for whole integer numbers.
     */
    default NumberFormat getIntegerFormatter() {
        NumberFormat nf = NumberFormat.getIntegerInstance(getLocale());
        if (nf instanceof DecimalFormat decimalFormat) {
            decimalFormat.setGroupingUsed(true);
        }
        return nf;
    }

    /**
     * Resolves an extended precision double formatter designed for material item weight scales.
     * 
     * @return A NumberFormat instance built for fractional floating-point representations.
     */
    default NumberFormat getDoubleFormatter() {
        NumberFormat nf = NumberFormat.getNumberInstance(getLocale());
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(4);
        if (nf instanceof DecimalFormat decimalFormat) {
            decimalFormat.setGroupingUsed(true);
        }
        return nf;
    }

    /**
     * Resolves a percentage formatting layout tailored for taxation or commercial discounts.
     * 
     * @return A specialized NumberFormat built for percentage value expressions.
     */
    default NumberFormat getPercentFormatter() {
        NumberFormat nf = NumberFormat.getPercentInstance(getLocale());
        nf.setMinimumFractionDigits(0);
        nf.setMaximumFractionDigits(2);
        return nf;
    }
        
    /**
     * Resolves the standard calendar date layout compliant with regional standards.
     * 
     * @return An unmodifiable DateTimeFormatter built for localized date representation.
     */
    default DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());
    }

    /**
     * Resolves a combined, immutable date and time audit trail signature pattern.
     * 
     * @return A modern localized DateTimeFormatter matching full calendar and clock markers.
     */
    default DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.MEDIUM).withLocale(getLocale());
    }

    /**
     * Resolves a precise time slice layout pattern capturing seconds boundaries.
     * 
     * @return A modern localized DateTimeFormatter mapping running hour, minute, and second sequences.
     */
    default DateTimeFormatter getTimeFormatter() {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withLocale(getLocale());
    }

    /**
     * Resolves an ergonomic user-facing timestamp completely stripped of running second indicators.
     * 
     * @return A modern localized DateTimeFormatter tracking concise hour and minute sequences.
     */
    default DateTimeFormatter getHourMinFormatter() {
        return DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(getLocale());
    }

    /**
     * Abstract Factory extension gateway used to resolve localized pluggable business services.
     * 
     * @param <T> The target service contract interface token type.
     * @param serviceClass The class mirror layout matching the requested regional business contract.
     * @return An active implementation instance provided by the vendor JAR, or null if unsupported.
     */
    default <T> T getLocalService(Class<T> serviceClass) {
        return null;
    }
}
