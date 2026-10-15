package com.openbravo.pos.printer.printer;

import java.awt.Component;

/**
 * Backward-compatible adapter delegating to {@link SelectPrinterPanel}.
 *
 * @deprecated Use {@link SelectPrinterPanel#show(Component, String[])} instead.
 * @author KriolOS
 */
@Deprecated
public class SelectPrinter {

    private final String printservice;
    private final boolean ok;

    private SelectPrinter(String printservice, boolean ok) {
        this.printservice = printservice;
        this.ok = ok;
    }

    /**
     * Backward-compatible factory method delegating to {@link SelectPrinterPanel}.
     *
     * @param parent candidate parent component
     * @param printers list of available printer names
     * @return adapter instance with selection result
     */
    public static SelectPrinter getSelectPrinter(Component parent, String[] printers) {
        String selected = SelectPrinterPanel.show(parent, printers);
        return new SelectPrinter(selected, selected != null);
    }

    public void setVisible(boolean visible) {
        // No-op for backward compatibility: SelectPrinterPanel.show() is executed synchronously in getSelectPrinter.
    }

    public boolean isOK() {
        return ok;
    }

    public String getPrintService() {
        return printservice;
    }
}
