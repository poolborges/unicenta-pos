package com.openbravo.ui.icon;

/**
 * Standard semantic icon specifications for KriolOS POS.
 * Decouples business domain actions from literal visual glyph names and legacy image formats.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public enum IconKey {

    CUSTOMER("user", "customer_sml.png"),
    USERS("users", "users.png"),
    PAY("credit-card", "pay.png"),
    CASH("banknote", "cash.png"),
    HOLD("pause-circle", null),
    PARKED("clock", "timer.png"),
    ORDERS("clipboard-list", "notes.png"),
    CASH_IN("arrow-down", "1downarrow.png"),
    CASH_OUT("arrow-up", "1uparrow.png"),
    CLOSE_CASH("lock", "password.png"),
    PLUS("plus", "btnplus.png"),
    MINUS("minus", "btnminus.png"),
    EDIT("pencil", "edit.png"),
    DELETE("trash", "sale_delete.png"),
    CLEAR("rotate-ccw", "reload.png"),
    DISCOUNT("tag", "discount.png"),
    SEARCH("search", "search24.png"),
    SETTINGS("gear", "configuration.png"),
    PRINTER("printer", "printer24.png"),
    LOCK("lock", "password.png"),
    UNLOCK("unlock", null),
    OK("check", "ok.png"),
    CANCEL("x", "cancel.png"),
    NAV_BACK("arrow-left", "1leftarrow.png"),
    NAV_FORWARD("arrow-right", "1rightarrow.png"),
    BACKSPACE("delete-left", "btnback.png"),
    DATABASE("database", "database.png");

    private final String literalName;
    private final String legacyFileName;

    IconKey(String literalName, String legacyFileName) {
        this.literalName = literalName;
        this.legacyFileName = legacyFileName;
    }

    /**
     * Returns the literal visual glyph name (e.g., "user", "credit-card", "arrow-left").
     *
     * @return literal icon name
     */
    public String getLiteralName() {
        return literalName;
    }

    /**
     * Returns the legacy PNG filename in com/openbravo/images/ or null if none.
     *
     * @return legacy PNG filename
     */
    public String getLegacyFileName() {
        return legacyFileName;
    }
}
