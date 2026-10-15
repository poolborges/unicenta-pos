package com.openbravo.ui.data.binding;

import java.util.regex.Pattern;

/**
 * A reusable validator for email addresses conforming to standard email specifications.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class EmailValidator implements Validator<String> {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private final String errorMessage;

    /**
     * Constructs an EmailValidator with a custom error message.
     *
     * @param errorMessage the message to display when validation fails
     */
    public EmailValidator(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public boolean isValid(String value) {
        if (value == null || value.trim().isEmpty()) {
            // Let asRequired handle empty states if needed; otherwise, consider empty as valid
            return true; 
        }
        return EMAIL_PATTERN.matcher(value.trim()).matches();
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
