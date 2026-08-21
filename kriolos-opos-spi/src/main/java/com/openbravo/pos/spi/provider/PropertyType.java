package com.openbravo.pos.spi.provider;

/**
 * Strongly-typed enumeration of supported data formats for pluggable configurations.
 * Bundles native Java compilation safety with robust text-based serialization mappings.
 * 
 * @author KriolOS POS
 * @since 1.0.0
 */
public enum PropertyType {
    
    /** Boolean flag variable rendered as graphical checkbox components */
    BOOLEAN("BOOLEAN"),

    /** Whole integer value processed via numeric text layouts */
    INTEGER("INTEGER"),

    /** Floating-point or general floating numeric variable data stream */
    NUMBER("NUMBER"),

    /** Single line alphanumeric input text field layout */
    STRING("STRING"),

    /** Expanded text area container allowing multi-line string payloads */
    MULTILINE_STRING("MULTILINESTRING"),

    /** Complex text content designed to hold active script engine routines */
    SCRIPT("SCRIPT"),
    
    /** Operational operating system path string pointing directly to a file */
    FILE("FILE"),
    
    /** Resource identifier targeting internal assets, templates or endpoint URLs */
    RESOURCE("RESOURCE"),
    
    /** Masked credentials container safeguarding passwords, hashes or private tokens */
    SECRET("SECRET");

    private final String code;

    /**
     * Internal constructor binding the immutable text serialization token.
     */
    PropertyType(String code) {
        this.code = code;
    }

    /**
     * Resolves the matching strongly-typed enum instance from a raw database or storage string token.
     * 
     * @param code The raw text representation retrieved from persistence layers.
     * @return The corresponding PropertyType enum constant.
     * @throws IllegalArgumentException If the provided text token does not match any core format.
     */
    public static PropertyType fromCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("Configuration property type code must never be null");
        }
        for (PropertyType type : values()) {
            if (type.code.equalsIgnoreCase(code.trim())) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown configuration property type code signature: " + code);
    }

    /**
     * Returns the standardized database-ready text representation of this configuration type.
     * 
     * @return The underlying immutable string code token.
     */
    @Override
    public String toString() {
        return this.code;
    }
}
