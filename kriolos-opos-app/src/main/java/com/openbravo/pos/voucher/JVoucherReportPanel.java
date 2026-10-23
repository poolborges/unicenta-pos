package com.openbravo.pos.voucher;

import com.openbravo.basic.AppLocal;
import com.openbravo.basic.BasicException;
import com.openbravo.data.gui.JMessagePanel;
import com.openbravo.data.gui.MessageInf;
import com.openbravo.data.gui.modal.PosUIModal;
import com.openbravo.pos.panels.AppView;
import com.openbravo.pos.reports.JRViewer400;
import com.openbravo.pos.reports.PrintReportUtils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/**
 * Modern modal panel for previewing and printing vouchers via JasperReports and {@link PosUIModal}.
 * Replaces legacy {@link JDialogReportPanel} with a panel-first modal architecture.
 *
 * @author KriolOS
 * @since 1.0.0
 */
public class JVoucherReportPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(JVoucherReportPanel.class.getName());

    private PosUIModal modalContext;
    private JRViewer400 reportViewer;
    private JPanel reportContainer;
    private JButton btnClose;

    public JVoucherReportPanel(AppView app, VoucherInfo voucherInfo, BufferedImage image) {
        initComponents();
        initDomainAdapters();
        loadReport(voucherInfo, image);
    }

    private void initComponents() {
        setLayout(new BorderLayout(0, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(840, 680));
        setMinimumSize(new Dimension(640, 480));

        reportContainer = new JPanel(new BorderLayout());
        reportContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        reportViewer = new JRViewer400(null);
        reportContainer.add(reportViewer, BorderLayout.CENTER);
        add(reportContainer, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 10, 0));
        actionsPanel.setOpaque(false);

        btnClose = new JButton(AppLocal.getIntString("button.close"));
        btnClose.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        btnClose.setPreferredSize(new Dimension(100, 38));
        btnClose.addActionListener(e -> handleClose());
        actionsPanel.add(btnClose);

        add(actionsPanel, BorderLayout.SOUTH);
    }

    private void initDomainAdapters() {
        setName("kriolos:voucher:report-panel");
        reportContainer.setName("kriolos:voucher:report-viewer");
        btnClose.setName("kriolos:voucher:btn-close");
    }

    @Override
    public void addNotify() {
        super.addNotify();
        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null) {
            root.setDefaultButton(btnClose);
        }
    }

    private void loadReport(VoucherInfo voucherInfo, BufferedImage image) {
        String templateFile = "com/openbravo/reports/voucher.jrxml";
        String res = "com/openbravo/reports/voucher_messages";

        try {
            Map<String, Object> reportParams = new HashMap<>();
            reportParams.put("CUSTOMER_NAME", voucherInfo.getCustomerName());
            reportParams.put("LOGO", image);
            reportParams.put("CODE", voucherInfo.getVoucherNumber());
            reportParams.put("ISSUED", new Date());
            reportParams.put("VALUE", voucherInfo.getAmount());
            reportParams.put("REPORT_RESOURCE_BUNDLE", ResourceBundle.getBundle(res));

            PrintReportUtils.loadReport(reportViewer, templateFile, reportParams);
        } catch (MissingResourceException | BasicException ex) {
            LOGGER.log(Level.WARNING, "Cannot load voucher report", ex);
            JMessagePanel.showMessage(this,
                    new MessageInf(MessageInf.SGN_WARNING, AppLocal.getIntString("message.cannotloadreport"), ex));
        }
    }

    public void setModalContext(PosUIModal modalContext) {
        this.modalContext = modalContext;
    }

    public String getTitle() {
        return AppLocal.getIntString("form.customertitle");
    }

    private void handleClose() {
        if (modalContext != null) {
            modalContext.close();
        }
    }

    /**
     * Displays the voucher report preview dialog via {@link PosUIModal}.
     *
     * @param parent candidate parent component
     * @param app POS application view
     * @param voucherInfo voucher data to render
     * @param image brand logo image or null
     */
    public static void show(Component parent, AppView app, VoucherInfo voucherInfo, BufferedImage image) {
        JVoucherReportPanel panel = new JVoucherReportPanel(app, voucherInfo, image);
        if (parent != null) {
            panel.applyComponentOrientation(parent.getComponentOrientation());
        }

        PosUIModal modal = PosUIModal.create(parent, panel)
                .setTitle(panel.getTitle())
                .setPreferredSize(panel.getPreferredSize())
                .setModal(true)
                .setResizable(true);

        panel.setModalContext(modal);
        modal.show();
    }
}
