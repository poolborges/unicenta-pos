package com.openbravo.pos.i18n.tool;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit test executing the I18nVerifier tool to verify all translation keys.
 */
public class I18nAuditTest {

    @Test
    @DisplayName("Verify that all translation modules can be audited without exceptions")
    void testAuditExecution() {
        assertDoesNotThrow(() -> {
            I18nVerifier.main(new String[]{"--details", "--log"});
        });
    }
}
