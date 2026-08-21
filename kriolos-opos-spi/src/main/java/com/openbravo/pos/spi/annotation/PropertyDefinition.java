package com.openbravo.pos.spi.annotation;

import com.openbravo.pos.spi.provider.PropertyType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.Repeatable;

/**
 * Annotation used to define a single configuration property metadata entry statically on a schema definition class.
 * Enforces compilation-time type safety for properties mapping directly to the POS graphical layout engine.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(PluginProperties.class)
public @interface PropertyDefinition {
    
    /** The unique internal structural key identifier utilized to store and fetch the property value. */
    String key();
    
    /** The default user-facing descriptive text displayed adjacent to the GUI input component. */
    String label();
    
    /** The resource bundle property key identifier used to translate the label component dynamically. */
    String i18nLabelKey();
    
    /** A detailed tooltip message explaining the purpose of this config block. */
    String description() default "";
    
    /** 
     * The text representation serialization of the target format type. 
     * Developers must pass string representations matching the core formatting types.
     */
    String type();
    
    /** Enforces validation constraints, preventing checkout execution if this parameter remains unassigned. */
    boolean required() default false;
    
    /** An optional baseline fallback string applied automatically during initial setup discovery phases. */
    String defaultValue() default "";
}
