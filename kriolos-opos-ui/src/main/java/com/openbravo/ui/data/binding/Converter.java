package com.openbravo.ui.data.binding;

/**
 * Defines the contract to transform data between the UI Presentation layer 
 * and the Domain Model layer.
 *
 * @param <P> Presentation type (usually String for text fields)
 * @param <M> Model type (e.g., Integer, Double, BigDecimal)
 * @author KriolOS
 * @since 1.0.0
 */
public interface Converter<P, M> {
    
    /**
     * Converts the presentation value to the model type.
     * 
     * @param presentationValue the value from the UI
     * @return the model value
     */
    M toModel(P presentationValue);

    /**
     * Converts the model value to the presentation type.
     * 
     * @param modelValue the value from the backend object
     * @return the presentation value
     */
    P toPresentation(M modelValue);
}
