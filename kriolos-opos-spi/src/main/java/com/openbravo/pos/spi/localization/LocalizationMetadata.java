package com.openbravo.pos.spi.localization;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to declare geographic metadata statically on point-of-sale implementations.
 * Allows the core engine to inventory supported locations without triggering premature class instantiation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface LocalizationMetadata {
    
    /**
     * Defines the ISO 639 language code of the localized plugin module.
     * 
     * @return The language string token.
     */
    String language();
    
    /**
     * Defines the ISO 3166 country code of the localized plugin module.
     * 
     * @return The country string token.
     */
    String country();
}
