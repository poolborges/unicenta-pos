package com.openbravo.pos.spi.localization;

import java.text.NumberFormat;
import java.text.DateFormat;
import java.util.Locale;

/**
 * Service Provider Interface (SPI) for regional POS localization extensions.
 * <p>
 * Third-party developers implement this interface to provision custom currency formatting, 
 * date configurations, numeric parsing rules, and localized downstream services (e.g., 
 * tax frameworks, electronic invoicing) tied to a specific geographic region.
 * </p>
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public interface LocalizationProvider {
    
    /**
     * Verifies if this provider handles the requested system locale.
     * 
     * @param locale The active runtime locale injected by the POS core engine.
     * @return {@code true} if this provider can handle compliance and formatting for the given locale.
     */
    boolean supports(Locale locale);
    
    /**
     * Resolves the customized currency formatter for the target region.
     * <p>
     * Implementation note: Regions without minor fractional units (such as CLP in Chile)
     * must explicitly clear or round fraction configurations here.
     * </p>
     * 
     * @return A thread-safe or newly instantiated {@link NumberFormat} configured for regional currency.
     */
    NumberFormat getCurrencyFormatter();
    
    /**
     * Resolves the calendar date formatting style compliant with regional standards.
     * 
     * @return A {@link DateFormat} instance ready for UI or receipt printing layouts.
     */
    DateFormat getDateFormatter();
    
    /**
     * Resolves the general numeric decimal/thousand grouping format for standard inventory counts.
     * 
     * @return A standard {@link NumberFormat} built for local numeric layouts.
     */
    NumberFormat getNumberFormatter();
    
    /**
     * Hook point for extensible, runtime-discovered business operations.
     * <p>
     * Functions as an Abstract Factory pattern variant. Allows the host POS system 
     * to request specialized services (like fiscal compliance bridges or electronic ticketing)
     * decoupled from the main application core.
     * </p>
     * 
     * @param <T> The operational type of the requested service interface.
     * @param serviceClass The class type defining the targeted regional business contract.
     * @return An active implementation instance provided by the third-party JAR, or {@code null} if unsupported.
     */
    default <T> T getLocalService(Class<T> serviceClass) {
        // Default implementation provides no extended fiscal services out of the box.
        return null;
    }
}
