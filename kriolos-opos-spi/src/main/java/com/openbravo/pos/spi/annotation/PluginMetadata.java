package com.openbravo.pos.spi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Unified annotation used to declare execution metadata statically on any pluggable POS component.
 * Maps components utilizing a strict URI-based service registration matrix for high-speed discovery.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginMetadata {
    
    /** The unique fully-qualified identification signature preventing classpath collisions. */
    String id();
    
    /** Defines the base structural SPI contract interface service implemented by this component. */
    Class<?> service();
    
    /** Points to the static metadata blueprint class containing the configuration property layout definitions. */
    Class<?> schema() default Void.class;
    
    /** 
     * Array of unique service registration URIs that this plugin supports.
     * Examples: {"l10n:pt-CV"}, {"device:printer:escpos"}, {"device:scale:serial"}
     */
    String[] selectors() default {};
}
