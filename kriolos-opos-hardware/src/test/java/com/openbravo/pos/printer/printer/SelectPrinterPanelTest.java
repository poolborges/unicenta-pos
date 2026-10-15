package com.openbravo.pos.printer.printer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Unit test suite for {@link SelectPrinterPanel}.
 *
 * @author KriolOS
 */
class SelectPrinterPanelTest {

    @Test
    @DisplayName("SelectPrinterPanel initializes with default and provided printer names")
    void testPanelInitialization() {
        String[] printers = new String[]{"EPSON TM-T20II", "Star TSP100", "PDF Virtual"};
        SelectPrinterPanel panel = new SelectPrinterPanel(printers);

        assertNotNull(panel);
        assertEquals("kriolos:printer:select-panel", panel.getName());
        assertFalse(panel.isAccepted());
        assertNull(panel.getSelectedPrintService());
        assertNotNull(panel.getTitle());
    }

    @Test
    @DisplayName("SelectPrinterPanel handles null or empty printer arrays gracefully")
    void testPanelWithNullPrinters() {
        SelectPrinterPanel panel = new SelectPrinterPanel(null);

        assertNotNull(panel);
        assertFalse(panel.isAccepted());
        assertNull(panel.getSelectedPrintService());
    }
}
