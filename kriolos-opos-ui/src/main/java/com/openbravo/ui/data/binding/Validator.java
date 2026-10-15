package com.openbravo.ui.data.binding;

import java.util.function.Predicate;

/**
 * Functional interface that validates a given model value.
 *
 * @param <T> the type of the value to validate
 * @author KriolOS
 * @since 1.0.0
 */
public interface Validator<T> {

    /**
     * Evaluates the value against the validation business rule.
     *
     * @param value the value to evaluate
     * @return true if valid, false if invalid
     */
    boolean isValid(T value);

    /**
     * Gets the error message to display when validation fails.
     *
     * @return the error message string
     */
    String getMessage();

    /**
     * Fluent utility method to create a validator from a predicate and a message.
     */
    static <T> Validator<T> from(Predicate<T> predicate, String errorMessage) {
        return new Validator<T>() {
            @Override public boolean isValid(T value) { return predicate.test(value); }
            @Override public String getMessage() { return errorMessage; }
        };
    }
}
