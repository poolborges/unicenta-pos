package com.openbravo.pos.spi.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares an SPI interface as an automatically discoverable Plugin Contract.
 * Any interface annotated with @PluginContract is dynamically discovered and
 * registered by PluginManager without requiring manual changes to the manager class.
 *
 * @author KriolOS POS
 * @since 1.0.0
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface PluginContract {

    /**
     * Category name or functional classification (e.g. "L10N", "I18N", "HARDWARE", "SCRIPTING").
     */
    String category() default "";

    /**
     * Human-readable description of the contract capabilities.
     */
    String description() default "";
}
