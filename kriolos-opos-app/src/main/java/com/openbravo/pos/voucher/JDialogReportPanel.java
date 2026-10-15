package com.openbravo.pos.voucher;

import com.openbravo.pos.panels.AppView;
import java.awt.Component;
import java.awt.image.BufferedImage;

/**
 * Backward-compatible adapter delegating to {@link JVoucherReportPanel}.
 *
 * @deprecated Use {@link JVoucherReportPanel#show(Component, AppView, VoucherInfo, BufferedImage)} instead.
 * @author KriolOS
 */
@Deprecated
public abstract class JDialogReportPanel {

    public static JDialogReportPanel getDialog(Component parent, AppView app, VoucherInfo voucherInfo, BufferedImage image) {
        JVoucherReportPanel.show(parent, app, voucherInfo, image);
        return new JDialogReportPanel() {
            @Override
            protected String getReport() {
                return "";
            }
        };
    }

    public void setVisible(boolean visible) {
        // No-op for backward compatibility: modal preview is executed synchronously in getDialog.
    }

    protected abstract String getReport();
}
